package com.master.data.management.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DataModelsOrchestrationService {

  private final ManageDataModelService manageDataModelService;

  //Manage DataModel Services.

  public void createAndAlterModel(JSONObject requestJson) throws Exception {
    //step1 create table with the required fields from json node
    manageDataModelService.upsertTable(requestJson);

    //step2 create Json schema on a fly for the object for next crud rest operations OR
    // keep these validations in another table to do these when acutal rest calls happened.
  }

  public List<String> getTablesList() {
    return manageDataModelService.getTablesList();
  }

  public List<String> getFieldsByTableName(String tableName) {
    return manageDataModelService.getFieldNamesByTableName(tableName);
  }

  public void createAndAlterCustomField(JSONObject requestJson) {
    manageDataModelService.upsertCustomField(requestJson);
  }

  //Manage Dataflow Services.


}
