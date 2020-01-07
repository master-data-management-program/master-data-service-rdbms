package com.mdm.DynamicTable.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface CreateDataModelService {

  void createTable(JsonNode createTableSqlString) throws Exception;
}
