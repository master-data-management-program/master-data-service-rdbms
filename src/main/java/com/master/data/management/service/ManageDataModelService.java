package com.master.data.management.service;

import java.util.List;
import org.json.simple.JSONObject;

public interface ManageDataModelService {

  void upsertTable(JSONObject createTableSqlString) throws Exception;

  List<String> getTablesList();

  List<String> getFieldNamesByTableName(String tableName);
}
