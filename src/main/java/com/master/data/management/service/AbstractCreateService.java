package com.master.data.management.service;

import static com.master.data.management.utils.ApplicationConstants.DATA_TYPE_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.DEFAULT_VALUE_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.MANDATORY_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.SPACE_STR;
import static com.master.data.management.utils.ApplicationConstants.TABLE_FIELD_NAME_JSON_KEY;
import static com.master.data.management.utils.ApplicationConstants.VALIDATIONS_JSON_KEY;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.master.data.management.dao.DataModelDAO;
import com.master.data.management.dto.TableDataType;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class AbstractCreateService {

  protected DataModelDAO dataModelDAO;


  protected void generatePrimaryKeyForCreateStmt(JSONArray primaryKeys,
      StringBuilder sqlBuilder,
      String tableName) {
    if (isNotEmpty(primaryKeys)) {
      sqlBuilder
          .append(",");
      generatePrimaryKey(primaryKeys, sqlBuilder, tableName);
    }
  }

  private void generatePrimaryKey(JSONArray primaryKeys, StringBuilder sqlBuilder,
      String tableName) {
    if (isNotEmpty(primaryKeys)) {
      AtomicInteger count = new AtomicInteger();

      sqlBuilder
          .append(SPACE_STR)
          .append("CONSTRAINT")
          .append(SPACE_STR)
          .append(tableName.toUpperCase() + "_PK")
          .append(" PRIMARY KEY (");

      primaryKeys.forEach(pk -> {
        sqlBuilder.append((String) pk);
        if (count.getAndIncrement() != (primaryKeys.size() - 1)) {
          sqlBuilder.append(",");
        }
      });
      sqlBuilder.append(")");

    }
  }

  protected void generateForeignKeys(Object reference, StringBuilder sqlBuilder,
      String tableName) {
    if (nonNull(reference)) {
      sqlBuilder.append(",");
      JSONObject referenceNode = (JSONObject) reference;
      prepareForeignKeySql(sqlBuilder, tableName, referenceNode);
    }
  }

  private void prepareForeignKeySql(StringBuilder sqlBuilder, String tableName,
      JSONObject referenceNode) {
    String referTableName = (String) referenceNode.get("table");
    String byField = (String) referenceNode.get("byField");
    sqlBuilder.append(SPACE_STR)
        .append("CONSTRAINT")
        .append(SPACE_STR)
        .append(prepareForeignKeyName(tableName, byField) + " FOREIGN KEY (")
        .append(byField)
        .append((") REFERENCES "))
        .append(referTableName)
        .append("(")
        .append(referenceNode.get("field"))
        .append(")");
  }

  private String prepareForeignKeyName(String tableName, String byField) {
    return "FK_" + tableName + "_" + byField;
  }

  protected void autoGenerateIdField(StringBuilder sqlBuilder) {
    sqlBuilder.append("id");
    sqlBuilder.append(SPACE_STR);
    sqlBuilder.append(TableDataType.UUID.getName());
    sqlBuilder.append(SPACE_STR);
  }

  protected void generateFieldsSql(JSONArray fieldsArray, StringBuilder sqlBuilder) {
    for (Object node : fieldsArray) {
      sqlBuilder.append(",");

      JSONObject fieldNode = new JSONObject((Map) node);
      generateFieldSql(sqlBuilder, fieldNode, false);
      generateNotNullSql(sqlBuilder, fieldNode);
    }
  }

  private void generateFieldSql(StringBuilder sqlBuilder, JSONObject fieldNode,
      boolean isAlterSql) {

    if (isAlterSql) {
      sqlBuilder.append("TYPE ");
    } else {
      sqlBuilder.append(fieldNode.get(TABLE_FIELD_NAME_JSON_KEY));
      sqlBuilder.append(SPACE_STR);
    }

    String dataType = (String) fieldNode.get(DATA_TYPE_JSON_KEY);
    TableDataType tableDataType = TableDataType.getDataType(dataType);
    sqlBuilder.append(tableDataType.getDataType());
//    boolean primaryKey = (Boolean) fieldNode.get("index");

    if (isAlterSql) {
      if (tableDataType == TableDataType.NUMBER) {
        sqlBuilder.append(" USING ").append(fieldNode.get(TABLE_FIELD_NAME_JSON_KEY))
            .append("::numeric");
      }
    }

    switch (tableDataType) {
      case NUMBER:
        extractNumberValidations(sqlBuilder, fieldNode);
        break;

      case STRING:
        extractStringValidations(sqlBuilder, fieldNode);
        break;

      default:
        throw new IllegalArgumentException("No Match found for table datatype::" + tableDataType);
    }

  }

  private void generateNotNullSql(StringBuilder sqlBuilder, JSONObject fieldNode) {
    JSONObject validations = new JSONObject((Map) fieldNode.get(VALIDATIONS_JSON_KEY));
    boolean isMandatory = (Boolean) validations.get(MANDATORY_JSON_KEY);

    //Adding nonnull constrains to fields
    if (isMandatory) {
      sqlBuilder.append(SPACE_STR);
      sqlBuilder.append("NOT NULL");
    }
  }

  private void extractStringValidations(StringBuilder sqlBuilder, JSONObject fieldNode) {
    Object validationsObj = fieldNode.get(VALIDATIONS_JSON_KEY);
    sqlBuilder.append("(");
    Object lengthObj = 20;

    if (nonNull(validationsObj)) {
      JSONObject validations = new JSONObject((Map) validationsObj);
      // retrieving Number length
      lengthObj = validations.get("length");
    }

    sqlBuilder.append(lengthObj);
    sqlBuilder.append(")");
  }

  private void extractNumberValidations(StringBuilder sqlBuilder, JSONObject fieldNode) {
    JSONObject validations = new JSONObject((Map) fieldNode.get(VALIDATIONS_JSON_KEY));

    // retrieving Number length
    Object lengthObj = validations.get("length");
    sqlBuilder.append("(");
    if (nonNull(lengthObj)) {
      sqlBuilder.append(lengthObj);

      // retrieving Number precision
      Object precisionObj = validations.get("precision");
      if (nonNull(precisionObj)) {
        sqlBuilder.append(",");
        sqlBuilder.append(precisionObj);
      }
    } else {
      sqlBuilder.append(20);
    }
    sqlBuilder.append(")");
  }


  protected void executeAlterFields(JSONObject fieldsChanges, String tableName) {
    prepareAndExecuteAlterAddFieldStatement(fieldsChanges, tableName);
    prepareAndExecuteAlterRenameFieldStatement(fieldsChanges, tableName);
    prepareAndExecuteAlterDeleteFieldStatement(fieldsChanges, tableName);
    prepareAndExecuteAlterUpdateFieldStatement(fieldsChanges, tableName);
  }

  private void prepareAndExecuteAlterUpdateFieldStatement(JSONObject fieldsChanges,
      String tableName) {
    Optional<JSONArray> modifiedFields = ofNullable(getArray(fieldsChanges, "modify"));
    // Add each rename field sql here

    StringBuilder sqlBuilder = new StringBuilder(SqlOperation.ALTER.getSql());
    sqlBuilder.append(tableName);
    AtomicInteger count = new AtomicInteger();
    modifiedFields.ifPresent(fields -> fields.forEach(field -> {
          JSONObject fieldObj = new JSONObject((Map) field);
          Map<String, Boolean> mapOfChanges = new LinkedHashMap<>();
          mapOfChanges.put(DATA_TYPE_JSON_KEY, nonNull(fieldObj.get(DATA_TYPE_JSON_KEY)));
          JSONObject validations = new JSONObject((Map) fieldObj.get(VALIDATIONS_JSON_KEY));
          mapOfChanges.put(DEFAULT_VALUE_JSON_KEY, nonNull(validations.get(DEFAULT_VALUE_JSON_KEY)));
          mapOfChanges.put(MANDATORY_JSON_KEY, nonNull(validations.get(MANDATORY_JSON_KEY)));
          AtomicInteger fieldChanges = new AtomicInteger();
          mapOfChanges.entrySet().forEach(
              entry -> {
                sqlBuilder.append(" ALTER COLUMN ");
                sqlBuilder.append(fieldObj.get(TABLE_FIELD_NAME_JSON_KEY));
                sqlBuilder.append(SPACE_STR);
                String key = entry.getKey();
                if (key.equalsIgnoreCase(DATA_TYPE_JSON_KEY)) {
                  generateFieldSql(sqlBuilder, fieldObj, true);
                } else if (key.equalsIgnoreCase(DEFAULT_VALUE_JSON_KEY)) {
                  sqlBuilder.append("SET ");
                  getDefaultValue(sqlBuilder, fieldObj);
                } else if (key.equalsIgnoreCase(MANDATORY_JSON_KEY)) {
                  if (nonNull(validations.get(MANDATORY_JSON_KEY).equals(true))) {
                    sqlBuilder.append(" SET NOT NULL");
                  } else {
                    sqlBuilder.append(" DROP NOT NULL");
                  }
                }
                if (fieldChanges.getAndIncrement() != mapOfChanges.size() - 1) {
                  sqlBuilder.append(",");
                }
              }
          );
          if (count.getAndIncrement() != fields.size() - 1) {
            sqlBuilder.append(",");
          }
        }
        )
    );
    dataModelDAO.executeSQL(sqlBuilder.toString());
  }

  private void prepareAndExecuteAlterRenameFieldStatement(JSONObject fieldsChanges,
      String tableName) {
    Optional<JSONArray> newFields = ofNullable(getArray(fieldsChanges, "rename"));
    // Add each rename field sql here

    newFields.ifPresent(fields -> fields.forEach(field -> {
          JSONObject fieldObj = new JSONObject((Map) field);
          StringBuilder sqlBuilder = new StringBuilder(SqlOperation.ALTER.getSql())
              .append(tableName)
              .append(" RENAME COLUMN ")
              .append(fieldObj.get("from"))
              .append(SPACE_STR)
              .append("TO")
              .append(SPACE_STR)
              .append(fieldObj.get("to"));

          dataModelDAO.executeSQL(sqlBuilder.toString());
        }
        )
    );
  }

  private void prepareAndExecuteAlterDeleteFieldStatement(JSONObject fieldsChanges,
      String tableName) {
    Optional<JSONArray> newFields = ofNullable(getArray(fieldsChanges, "remove"));

    // Add each remove field sql here
    newFields.ifPresent(fields -> fields.forEach(field -> {
          StringBuilder sqlBuilder = new StringBuilder(SqlOperation.ALTER.getSql())
              .append(tableName)
              .append(" DROP COLUMN ")
              .append(field);

          dataModelDAO.executeSQL(sqlBuilder.toString());
        }
        )
    );
  }

  private void prepareAndExecuteAlterAddFieldStatement(JSONObject fieldsChanges,
      String tableName) {
    StringBuilder sqlBuilder = new StringBuilder(SqlOperation.ALTER.getSql());
    sqlBuilder.append(tableName);
    Optional<JSONArray> newFields = ofNullable(getArray(fieldsChanges, "add"));
    // Add each add field sql here

    newFields.ifPresent(fields -> {
      AtomicInteger count = new AtomicInteger();
      fields.forEach(fieldNode -> {
        JSONObject field = new JSONObject((Map) fieldNode);
        sqlBuilder.append(SPACE_STR).append("ADD").append(SPACE_STR);
        generateFieldSql(sqlBuilder, field, false);
        getDefaultValue(sqlBuilder, field);
        generateNotNullSql(sqlBuilder, field);

        if (count.getAndIncrement() != fields.size() - 1) {
          sqlBuilder.append(",");
        }
      });
    });

    // execute sql to create alter ADD statement.

    dataModelDAO.executeSQL(sqlBuilder.toString());
  }

  private void getDefaultValue(StringBuilder sqlBuilder, JSONObject fieldNode) {
    String dataType =
        nonNull(fieldNode.get(DATA_TYPE_JSON_KEY)) ? (String) fieldNode.get(DATA_TYPE_JSON_KEY)
            : null;
    JSONObject validations = new JSONObject((Map) fieldNode.get(VALIDATIONS_JSON_KEY));
    Object defaultValue =
        nonNull(validations.get(DEFAULT_VALUE_JSON_KEY)) ? validations.get(
            DEFAULT_VALUE_JSON_KEY)
            : null;
    if (nonNull(defaultValue)) {
      if (!dataType.equals("boolean")
          && TableDataType.getDataType(dataType) != TableDataType.NUMBER) {
        defaultValue = "'" + (String) validations.get(DEFAULT_VALUE_JSON_KEY) + "'";
      }
      sqlBuilder.append(" DEFAULT ").append(defaultValue);
    }
  }


  protected void executeAlterTable(JSONObject tableChange, String tableName) {
    ofNullable(tableChange.get("rename"))
        .ifPresent(renameValue -> {
          StringBuilder sqlBuilder = new StringBuilder(SqlOperation.ALTER.getSql())
              .append(tableName)
              .append(" RENAME TO ")
              .append(renameValue);

          dataModelDAO.executeSQL(sqlBuilder.toString());
        });

    //Need to implement status field as well.CONSTRAINT
  }

  protected void executeAlterConstraints(JSONObject constraintChanges,
      String tableName) {

    //references
    ofNullable(getObject(constraintChanges, "references"))
        .ifPresent(references -> {
          //add constraints
          executeAlterAddReferenceSql(constraintChanges, tableName, "add");

          //remove constraints
          executeAlterDropForeinKeySql(constraintChanges, tableName);
        });

    //remove primarykeys
    ofNullable(getArray(constraintChanges, "primaryKeys"))
        .ifPresent(primaryKeys -> {

          //remove existing primary key
          executeAlterDropConstraint(tableName, tableName.toUpperCase() + "_PK");

          //create new primary keys
          StringBuilder sqlBuilder = new StringBuilder(SqlOperation.ALTER.getSql())
              .append(tableName).append(SPACE_STR).append("ADD ");
          generatePrimaryKey(primaryKeys, sqlBuilder, tableName);
          dataModelDAO.executeSQL(sqlBuilder.toString());
        });
  }

  private void executeAlterDropForeinKeySql(JSONObject constraintChanges, String tableName) {
    ofNullable(getObject(constraintChanges, "drop"))
        .ifPresent(dropReference -> {
          String constraintName = prepareForeignKeyName(tableName,
              (String) dropReference.get("byField"));
          executeAlterDropConstraint(tableName, constraintName);
        });
  }

  private void executeAlterDropConstraint(String tableName, String constraintName) {
    StringBuilder sqlBuilder = new StringBuilder(SqlOperation.ALTER.getSql())
        .append(tableName)
        .append(SPACE_STR)
        .append("DROP CONSTRAINT")
        .append(SPACE_STR)
        .append(constraintName);

    dataModelDAO.executeSQL(sqlBuilder.toString());
  }

  private void executeAlterAddReferenceSql(JSONObject constraintChanges, String tableName,
      String sqlType) {
    ofNullable(getObject(constraintChanges, sqlType))
        .ifPresent(addReference -> {
          StringBuilder sqlBuilder = new StringBuilder(SqlOperation.ALTER.getSql())
              .append(tableName)
              .append(SPACE_STR)
              .append(sqlType.toUpperCase())
              .append(SPACE_STR);
          prepareForeignKeySql(sqlBuilder, tableName, addReference);

          dataModelDAO.executeSQL(sqlBuilder.toString());
        });
  }


  protected JSONObject getObject(JSONObject jsonObject, String key) {
    Object o = jsonObject.get(key);
    return nonNull(o) ? new ObjectMapper().convertValue(o, JSONObject.class) : null;
  }

  protected JSONArray getArray(JSONObject jsonObject, String key) {
    Object obj = jsonObject.get(key);
    return nonNull(obj) ? new ObjectMapper().convertValue(obj, JSONArray.class) : null;
  }

  private Object readJsonFromString(String jsonBody) throws Exception {
    JSONParser jsonParser = new JSONParser();
    return jsonParser.parse(jsonBody);
  }

}
