package com.mdm.DynamicTable.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.mdm.DynamicTable.dto.DataModelResponse;
import java.io.IOException;

public interface DataModelService {

//  DataModelResponse createDataModel(String collectionName, String indexId, JsonNode requestJson)
//      throws IOException;
//
//  void amendMasterTableRecord(String collectionName, JsonNode requestJson);

  void createTable(String createTableSqlString);
}
