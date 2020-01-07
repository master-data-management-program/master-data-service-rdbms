package com.master.data.management.service;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.master.data.management.dao.DataModelDAO;
import com.master.data.management.dto.TableDataType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CreateDataModelServiceImpl implements CreateDataModelService {

  private static final String SPACE_STR = " ";

  @Autowired
  private DataModelDAO dataModelDAO;

  @Autowired
  private ObjectMapper mapper;

  @Override
  @Transactional
  public void createTable(JsonNode requestJson) throws Exception {
    String createTableSqlString = createTableFromRequestNode(
        mapper.writeValueAsString(requestJson));
    dataModelDAO.executeCreateTable(createTableSqlString);
  }

  @Override
  @Transactional
  public List<String> getTablesList() {
    return dataModelDAO.getTablesFromDatabase();
  }

  @Override
  @Transactional
  public List<String> getFieldNamesByTableName(String tableName) {
    return dataModelDAO.getFieldNamesByTableName(tableName);
  }


  private String createTableFromRequestNode(String requestJson) throws Exception {
    StringBuilder newTableSqlBuilder = new StringBuilder("CREATE TABLE ");
    JSONObject jsonObject = (JSONObject) readJsonSimpleDemo(requestJson);
    String tableName = (String) jsonObject.get("tableName");
    JSONArray fieldsArray = (JSONArray) jsonObject.get("fields");
    Object reference = jsonObject.get("reference");

    newTableSqlBuilder.append(tableName);
    newTableSqlBuilder.append("(");
    //Auto generated id filed will be added by default to each table
    autoGenerateIdField(newTableSqlBuilder);
    //Provided table fields will be added to sql script
    generateFieldsSql(fieldsArray, newTableSqlBuilder);
    generateForeignKeys(reference, newTableSqlBuilder, tableName);
    newTableSqlBuilder.append(")");
    log.info("Generated Sql:");
    log.info(newTableSqlBuilder.toString());

    return newTableSqlBuilder.toString();
  }

  private void generateForeignKeys(Object reference, StringBuilder newTableSqlBuilder,
      String tableName) {
    if (nonNull(reference)) {
      newTableSqlBuilder.append(",");
      JSONObject referenceNode = (JSONObject) reference;
      String referTableName = (String) referenceNode.get("table");
      String byField = (String) referenceNode.get("byField");
      newTableSqlBuilder.append(SPACE_STR);
      newTableSqlBuilder.append("CONSTRAINT FK_" + tableName + "_" + byField + " FOREIGN KEY (");
      newTableSqlBuilder.append(byField);
      newTableSqlBuilder.append((") REFERENCES "));
      newTableSqlBuilder.append(referTableName);
      newTableSqlBuilder.append("(");
      newTableSqlBuilder.append(referenceNode.get("field"));
      newTableSqlBuilder.append(")");
    }
  }

  private void autoGenerateIdField(StringBuilder newTableSqlBuilder) {
    newTableSqlBuilder.append("id");
    newTableSqlBuilder.append(SPACE_STR);
    newTableSqlBuilder.append(TableDataType.NUMBER.getName());
    newTableSqlBuilder.append(SPACE_STR);
    newTableSqlBuilder.append("GENERATED BY DEFAULT AS IDENTITY");
  }

  private void generateFieldsSql(JSONArray fieldsArray, StringBuilder newTableSqlBuilder) {
    for (Object node : fieldsArray) {
      newTableSqlBuilder.append(",");

      JSONObject fieldNode = (JSONObject) node;
      newTableSqlBuilder.append(fieldNode.get("name"));
      newTableSqlBuilder.append(SPACE_STR);

      String dataType = (String) fieldNode.get("datatype");
      TableDataType tableDataType = TableDataType.getDataType(dataType);
      newTableSqlBuilder.append(tableDataType.getDataType());
      boolean primaryKey = (Boolean) fieldNode.get("index");
      JSONObject validations = (JSONObject) fieldNode.get("validations");
      boolean isMandatory = (Boolean) validations.get("mandatory");

      switch (tableDataType) {
        case NUMBER:
          extractNumberValidations(newTableSqlBuilder, fieldNode);
          break;

        case STRING:
          extractStringValidations(newTableSqlBuilder, fieldNode);
          break;

        default:
          throw new IllegalArgumentException("No Match found for table datatype::" + tableDataType);
      }

      //Adding primarykey and nonnull constrains to fields
      if (primaryKey) {
        newTableSqlBuilder.append(SPACE_STR);
        newTableSqlBuilder.append("PRIMARY KEY");
      } else if (isMandatory) {
        newTableSqlBuilder.append(SPACE_STR);
        newTableSqlBuilder.append("NOT NULL");
      }
    }
  }

  private void extractStringValidations(StringBuilder newTableSqlBuilder, JSONObject fieldNode) {
    Object validationsObj = fieldNode.get("validations");
    newTableSqlBuilder.append("(");
    Object lengthObj = 20;

    if (nonNull(validationsObj)) {
      JSONObject validations = (JSONObject) validationsObj;
      // retrieving Number length
      lengthObj = validations.get("length");
    }

    newTableSqlBuilder.append(lengthObj);
    newTableSqlBuilder.append(")");
  }

  private void extractNumberValidations(StringBuilder newTableSqlBuilder, JSONObject fieldNode) {
    JSONObject validations = (JSONObject) fieldNode.get("validations");

    // retrieving Number length
    Object lengthObj = validations.get("length");
    newTableSqlBuilder.append("(");
    if (nonNull(lengthObj)) {
      newTableSqlBuilder.append(lengthObj);

      // retrieving Number precision
      Object precisionObj = validations.get("precision");
      if (nonNull(precisionObj)) {
        newTableSqlBuilder.append(",");
        newTableSqlBuilder.append(precisionObj);
      }
    } else {
      newTableSqlBuilder.append(20);
    }
    newTableSqlBuilder.append(")");
  }


  private Object readJsonSimpleDemo(String jsonBody) throws Exception {
    JSONParser jsonParser = new JSONParser();
    return jsonParser.parse(jsonBody);
  }
}