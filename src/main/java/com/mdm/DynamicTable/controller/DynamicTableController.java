package com.mdm.DynamicTable.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mdm.DynamicTable.dto.DataModelResponse;
import com.mdm.DynamicTable.service.DataModelsOrchestrationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("${data.endpoint}/models")
@RestController
@Slf4j
public class DynamicTableController {

  @Autowired
  private DataModelsOrchestrationService orchestrationService;

  @PostMapping
  public ResponseEntity<String> createNewEntity(
      @RequestBody JsonNode requestJson) throws Exception {

    //step1 Json schema validation needs to be handled for create table

    //step2 create table with provided fields
    DataModelResponse dataModelResponse = orchestrationService
        .createDataModel(requestJson);

    //step3 return the response back to consumer
    return ResponseEntity
        .status(dataModelResponse.getHttpStatus())
        .body(dataModelResponse.getStatusMessage());
  }


//  @PutMapping
//  public ResponseEntity<String> updateEntity(
//      @RequestParam("tableName") String collectionName,
//      @RequestBody JsonNode requestJson) {
//
//    DataModelResponse dataModelResponse = orchestrationService
//        .updateDataModel(collectionName, requestJson);
//
//    return ResponseEntity
//        .status(dataModelResponse.getHttpStatus())
//        .body(dataModelResponse.getStatusMessage());
//  }
//
//
//  @PutMapping
//  public ResponseEntity<String> updateEntity(
//      @RequestParam("tableName") String collectionName,
//      @RequestParam("indexId") String indexId,
//      @RequestBody JsonNode requestJson) {
//
//    DataModelResponse dataModelResponse = orchestrationService
//        .deleteDataModel(collectionName, requestJson);
//
//    return ResponseEntity
//        .status(dataModelResponse.getHttpStatus())
//        .body(dataModelResponse.getStatusMessage());
//  }


//  @DeleteMapping
//  public ResponseEntity<String> dropEntity(
//      @RequestParam("tableName") String collectionName) {
//
//    DataModelResponse dataModelResponse = orchestrationService
//        .createDataModel(collectionName, indexId, requestJson);
//
//    return ResponseEntity
//        .status(dataModelResponse.getHttpStatus())
//        .body(dataModelResponse.getStatusMessage());
//  }
}
