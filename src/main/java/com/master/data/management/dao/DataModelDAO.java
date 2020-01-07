package com.master.data.management.dao;

import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataModelDAO {

  public static final String GET_ALL_TABLES_FROM_DATABASE = "select TNAME from tab where TABTYPE like 'TABLE'";
  private String GET_ALL_FIELDS_FROM_TABLE = "select COLUMN_NAME from ALL_TAB_COLUMNS where TABLE_NAME='%s'";

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @PostConstruct
  void init() {
    // o_name and O_NAME, same
    jdbcTemplate.setResultsMapCaseInsensitive(true);
  }

  public void executeCreateTable(String createTableSqlString) {
    log.info("Executing Create Table Stored Procedure.");
    jdbcTemplate.execute(createTableSqlString);
    log.info("Table Created Successfully.");
  }

  public List<String> getTablesFromDatabase() {
    return jdbcTemplate.queryForList(GET_ALL_TABLES_FROM_DATABASE, String.class);
  }

  public List<String> getFieldNamesByTableName(String tableName) {
    return jdbcTemplate
        .queryForList(String.format(GET_ALL_FIELDS_FROM_TABLE, tableName), String.class);
  }
}
