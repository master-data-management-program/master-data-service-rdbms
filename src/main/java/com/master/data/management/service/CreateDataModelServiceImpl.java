package com.master.data.management.service;

import static java.util.Optional.ofNullable;

import com.master.data.management.dao.DataModelDAO;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CreateDataModelServiceImpl extends AbstractCreateService implements
    CreateDataModelService {

  @Autowired
  public CreateDataModelServiceImpl(DataModelDAO dataModelDAO) {
    this.dataModelDAO = dataModelDAO;
  }

  @Override
  @Transactional
  public void upsertTable(JSONObject jsonObject) {
    String sqlType = (String) jsonObject.get("operation");
    SqlOperation sqlOperation = SqlOperation.getSqlOperation(sqlType);

    StringBuilder sqlBuilder = new StringBuilder(sqlOperation.getSql());

    String tableName = (String) jsonObject.get(TABLE_NAME_JSON_KEY);
    sqlBuilder.append(tableName);
    sqlBuilder.append("(");

    if (SqlOperation.CREATE == sqlOperation) {

      JSONArray fieldsArray = getArray(jsonObject, FIELDS_JSON_KEY);
      JSONArray primaryKeys = getArray(jsonObject, PRIMARY_KEYS_JSON_KEY);
      Object reference = jsonObject.get(REFERENCE_JSON_KEY);

      //Auto generated id filed will be added by default to each table
      autoGenerateIdField(sqlBuilder);
      //Provided table fields will be added to sql script
      generateFieldsSql(fieldsArray, sqlBuilder);
      generatePrimaryKeyForCreateStmt(primaryKeys, sqlBuilder, tableName);
      generateForeignKeys(reference, sqlBuilder, tableName);

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
  }


  @Override
  public List<String> getTablesList() {
    return dataModelDAO.getTablesFromDatabase();
  }

  @Override
  public List<String> getFieldNamesByTableName(String tableName) {
    return dataModelDAO.getFieldNamesByTableName(tableName);
  }
}