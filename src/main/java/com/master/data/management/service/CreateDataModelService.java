package com.master.data.management.service;

import java.util.List;
import org.json.simple.JSONObject;

public interface CreateDataModelService {

  void upsertTable(JSONObject createTableSqlString) throws Exception;

  List<String> getTablesList();

  List<String> getFieldNamesByTableName(String tableName);
}
