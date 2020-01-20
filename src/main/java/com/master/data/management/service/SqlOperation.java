package com.master.data.management.service;

import java.util.Arrays;

public enum SqlOperation {
  CREATE("create", "CREATE TABLE "),

  ALTER("alter", "ALTER TABLE ");

  String type;
  String sql;

  SqlOperation(String type, String sql) {
    this.type = type;
    this.sql = sql;
  }

  public String getSql() {
    return this.sql;
  }

  public String getType() {
    return this.type;
  }


  public static SqlOperation getSqlOperation(String type) {
    return Arrays.stream(SqlOperation.values())
        .filter(op -> op.type.equalsIgnoreCase(type))
        .findFirst()
        .orElseThrow(IllegalArgumentException::new);
  }
}
