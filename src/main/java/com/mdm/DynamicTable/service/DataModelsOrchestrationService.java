package com.mdm.DynamicTable.service;

import static org.springframework.http.HttpStatus.OK;

import com.fasterxml.jackson.databind.JsonNode;
import com.mdm.DynamicTable.dto.DataModelResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataModelsOrchestrationService {

  private final CreateDataModelService createDataModelService;

  public DataModelResponse createDataModel(JsonNode requestJson) throws Exception {
    //step1 create table with the required fields from json node
    createDataModelService.createTable(requestJson);

    //step2 create Json schema on a fly for the object for next crud rest operations OR
    // keep these validations in another table to do these when acutal rest calls happened.

    // return DataModelResponse
    return DataModelResponse.builder().httpStatus(OK).build();
  }

  public List<String> getTablesList(){
    return createDataModelService.getTablesList();
  }

  public List<String> getFieldsByTableName(String tableName) {
    return createDataModelService.getFieldNamesByTableName(tableName);
  }
}
