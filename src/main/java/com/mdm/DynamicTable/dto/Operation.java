package com.mdm.DynamicTable.dto;

import java.util.Arrays;

public enum Operation {
  CREATE_TABLE("createTable"),
  ALTER_TABLE("alterTable"),
  CREATE_INDEX("createIndex"),

  DROP_TABLE("dropTable"),
  DROP_INDEX("dropIndex"),
  DROP_COLUMN("dropColumn");

  private String name;

  public Operation getOperation(String name) {
    return Arrays.stream(Operation.values())
        .filter(operation -> operation.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }


  Operation(String createTable) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
