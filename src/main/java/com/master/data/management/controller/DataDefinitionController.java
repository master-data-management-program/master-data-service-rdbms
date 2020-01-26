package com.master.data.management.controller;

import static com.master.data.management.utils.ApplicationConstants.OPERATION_JSON_KEY;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.master.data.management.jpa.entities.CustomField;
import com.master.data.management.service.DataModelsOrchestrationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Api("This controller endpoints are used to (DDL) create and manage tables")
@RequestMapping("${data.endpoint}/tables")
@Slf4j
@RestController
public class DataDefinitionController {

  @Autowired
  private DataModelsOrchestrationService orchestrationService;

  @ApiOperation(value = "UpSertDataModel endpoint to create and alter table in database with the provided json request.", consumes = "application/hal+json", produces = "application/json")
  @PostMapping(produces = "application/json", consumes = "application/json")
  public ResponseEntity<String> upsertDataModel(
      @RequestBody JSONObject requestJson) throws Exception {
    if (isNull(requestJson)) {
      throw new IllegalArgumentException("Request body must not be empty.");
    }
    Object operation = requestJson.get(OPERATION_JSON_KEY);
    HttpStatus status = CREATED;
    String responseBody = "Successfully Entity has been created.";

    //step1 Json schema validation needs to be handled for create table

    //step2 create table with provided fields
    orchestrationService.createAndAlterModel(requestJson);

    //step3 return the response back to consumer
    if (nonNull(operation) && !operation.equals("create")) {
      status = OK;
      responseBody = "Successfully Entity has been updated.";
    }

    return ResponseEntity.status(status).body(responseBody);
  }

  @ApiOperation(value = "Retrieves all tablenames which are active in the database.", produces = "application/json")
  @GetMapping(produces = "application/json")
  public ResponseEntity<List<String>> getTablesList() {
    List<String> tablesList = orchestrationService.getTablesList();
    return ResponseEntity.status(OK).body(tablesList);
  }

  @ApiOperation(value = "Retrieves all fields names from the database,for the provided table name.", produces = "application/json")
  @GetMapping(value = "{tableName}/fields", produces = "application/json")
  public ResponseEntity<List<String>> getFieldsByTableName(
      @PathVariable("tableName") String tableName) {
    List<String> fieldsList = orchestrationService.getFieldsByTableName(tableName);
    return ResponseEntity.status(OK).body(fieldsList);
  }

  @ApiOperation(value = "Retrieves all custom field structures from the database", produces = "application/json")
  @GetMapping(value = "/customFields", produces = "application/json")
  public ResponseEntity<List<CustomField>> getAllCustomFields() {
    List<CustomField> fieldsList = orchestrationService.getCustomFields();
    return ResponseEntity.status(OK).body(fieldsList);
  }

  @ApiOperation(value = "Create custom field structure", produces = "application/json")
  @ResponseStatus(CREATED)
  @PostMapping(value = "/customFields", produces = "application/json")
  public void createNewCustomField(@RequestBody JSONObject requestJson) {
    orchestrationService.createNewCustomField(requestJson);
  }

//  @ApiOperation(value = "Alter entity endpoint to alter table in database with the provided json request.", consumes = "application/hal+json", produces = "application/json")
//  @PutMapping(produces = "application/json", consumes = "application/hal+json")
//  public ResponseEntity<String> alterEntity(
//      @RequestBody JsonNode requestJson) throws Exception {
//
//    step1 Json schema validation needs to be handled for create table
//    step2 create table with provided fields
//    DataModelResponse dataModelResponse = orchestrationService
//        .alterDataModel(requestJson);
//
//    step3 return the response back to consumer
//    return ResponseEntity
//        .status(dataModelResponse.getHttpStatus())
//        .body(dataModelResponse.getStatusMessage());
//  }
}

