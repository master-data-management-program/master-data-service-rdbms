package com.master.data.management.service;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public interface CreateDataModelService {

  void createTable(JsonNode createTableSqlString) throws Exception;

  List<String> getTablesList();

  List<String> getFieldNamesByTableName(String tableName);
}
