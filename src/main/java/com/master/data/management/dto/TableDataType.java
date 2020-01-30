package com.master.data.management.dto;

import java.util.Arrays;

public enum TableDataType {
  STRING("String", 's', "VARCHAR"),
  NUMBER("Number", 'n', "NUMERIC"),
  DATE_ONLY("DateOnly", 'd', "DATE"),
  BOOLEAN("boolean", 'b', "BOOLEAN"),
  BIGINT("bigint", 'g', "bigint"),
  TIMESTAMP_WITH_ZONE("timestamp", 't', "TIMESTAMP"),
  TIMESTAMP_WITHOUT_ZONE("timestampWithUTC", 'z', "TIMESTAMPZ"),
  UUID("UUID", 'i', "UUID");

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
