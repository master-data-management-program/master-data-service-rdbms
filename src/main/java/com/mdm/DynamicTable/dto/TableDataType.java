package com.mdm.DynamicTable.dto;

import java.util.Arrays;

public enum TableDataType {
  STRING("String", 's', "VARCHAR2"),
  NUMBER("Number", 'n', "NUMBER");

  String name;
  char key;
  String dataType;

  TableDataType(String name, char key, String dataType) {
    this.name = name;
    this.key = key;
    this.dataType = dataType;
  }

  public String getName() {
    return this.name;
  }

  public String getDataType() {
    return this.dataType;
  }

  public char getKey() {
    return this.key;
  }

  public static TableDataType getDataType(String name) {
    return Arrays.stream(TableDataType.values())
        .filter(type -> type.getName().equalsIgnoreCase(name))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }

}
