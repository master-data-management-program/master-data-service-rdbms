package com.master.data.management.service;

import static com.master.data.management.utils.ApplicationConstants.CHANGES_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.CONSTRAINTS_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.FIELDS_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.OPERATION_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.PRIMARY_KEYS_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.TABLE_FIELD_NAME_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.TABLE_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.TABLE_NAME_JSON_KEY;
import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.master.data.management.dao.DataModelDAO;
import com.master.data.management.jpa.entities.CustomFieldEntity;
import com.master.data.management.jpa.entities.CustomFieldEntity.CustomFieldEntityBuilder;
import com.master.data.management.jpa.entities.TableVersionEntity;
import com.master.data.management.jpa.repos.CustomFieldRepository;
import com.master.data.management.jpa.repos.TableVersionsRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ManageDataModelServiceImpl extends AbstractCreateService implements
    ManageDataModelService {

  private ObjectMapper mapper;

  @Autowired
  public ManageDataModelServiceImpl(DataModelDAO dataModelDAO,
      TableVersionsRepository tableVersionsRepository,
      CustomFieldRepository customFieldRepository,
      ObjectMapper mapper) {
    this.dataModelDAO = dataModelDAO;
    this.tableVersionsRepository = tableVersionsRepository;
    this.customFieldRepository = customFieldRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional
  public void upsertTable(JSONObject jsonObject) {
    String sqlType = (String) jsonObject.get(OPERATION_JSON_KEY);
    SqlOperation sqlOperation = SqlOperation.getSqlOperation(sqlType);

    StringBuilder sqlBuilder = new StringBuilder(sqlOperation.getSql());

    String tableName = (String) jsonObject.get(TABLE_NAME_JSON_KEY);
    sqlBuilder.append(tableName);
    sqlBuilder.append("(");

    if (SqlOperation.CREATE == sqlOperation) {

      JSONArray fieldsArray = getArray(jsonObject, FIELDS_JSON_KEY);
      JSONArray primaryKeys = getArray(jsonObject, PRIMARY_KEYS_JSON_KEY);

      //Auto generated id filed will be added by default to each table
      autoGenerateIdField(sqlBuilder);
      //Provided table fields will be added to sql script
      generateFieldsSql(fieldsArray, sqlBuilder, tableName);
      generatePrimaryKeyForCreateStmt(primaryKeys, sqlBuilder, tableName);

      sqlBuilder.append(")");
      log.info("Generated Sql:");
      log.info(sqlBuilder.toString());

      dataModelDAO.executeSQL(sqlBuilder.toString());

    } else if (SqlOperation.ALTER == sqlOperation) {

      ofNullable(getObject(jsonObject, CHANGES_JSON_KEY))
          .ifPresent(changes -> {
                ofNullable(getObject(changes, FIELDS_JSON_KEY))
                    .ifPresent(fields -> executeAlterFields(fields, tableName));
                ofNullable(getObject(changes, CONSTRAINTS_JSON_KEY))
                    .ifPresent(constraints -> executeAlterConstraints(constraints, tableName));
                ofNullable(getObject(changes, TABLE_JSON_KEY))
                    .ifPresent(table -> executeAlterTable(table, tableName));
              }
          );

    } else {

      throw new UnsupportedOperationException("Sql Operation value must be alter or create.");
    }

    log.info("Update Successful.");
    updateTableDeltas(jsonObject, tableName);
  }


  @SneakyThrows
  private void updateTableDeltas(JSONObject jsonObject, String tableName) {
    String effective = mapper.writeValueAsString(jsonObject);
    Map<LocalDateTime, JSONObject> deltas = new HashMap<>();

    Optional<TableVersionEntity> entityOptional = tableVersionsRepository
        .findByTableName(tableName);

    TableVersionEntity tableVersionEntity = entityOptional
        .orElse(
            TableVersionEntity
                .builder()
                .tableName(tableName)
                .build()
        );

    if (entityOptional.isPresent()) {
      log.info("{} is existing table", tableName);
      try {
        deltas = mapper.convertValue(mapper.readTree(entityOptional.get().getDeltas()),
            new TypeReference<HashMap<LocalDateTime, JSONObject>>() {
            });
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    deltas.put(LocalDateTime.now(), jsonObject);

    tableVersionEntity.setEffective(effective);
    tableVersionEntity.setDeltas(mapper.writeValueAsString(deltas));

    tableVersionsRepository.save(tableVersionEntity);

    log.info("Table_Version successfully updated with latest JsonObject deltas.");
  }

  @Override
  public List<String> getTablesList() {
    return dataModelDAO.getTablesFromDatabase();
  }

  @Override
  public List<String> getFieldNamesByTableName(String tableName) {
    return dataModelDAO.getFieldNamesByTableName(tableName);
  }

  @SneakyThrows
  @Override
  public void upsertCustomField(JSONObject jsonObject) {
    String fieldName = String.valueOf(jsonObject.get(TABLE_FIELD_NAME_JSON_KEY));
    String effective = mapper.writeValueAsString(jsonObject);

    Optional<CustomFieldEntity> entityOptional = customFieldRepository
        .findByFieldName(fieldName);

    CustomFieldEntity customFieldEntity = entityOptional
        .orElse(
            CustomFieldEntity
                .builder()
                .id(ZonedDateTime.now().toEpochSecond())
                .fieldName(fieldName)
                .build()
        );

    if (entityOptional.isPresent()) {
      log.info("{} is existing in table", fieldName);
    }

    customFieldEntity.setCustomFieldJson(effective);

    customFieldRepository.save(customFieldEntity);

    log.info("Custom Field has been successfully updated with latest JsonObject.");

  }
}