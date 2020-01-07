package com.mdm.DynamicTable.dao;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataModelDAO {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @PostConstruct
  void init() {
    // o_name and O_NAME, same
    jdbcTemplate.setResultsMapCaseInsensitive(true);
  }

  public void executeCreateTable(String createTableSqlString) {
    log.info("Executing Create Table Stored Procedure.");
//    jdbcTemplate.execute(SQL_STORED_PROC);
    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbcTemplate.getDataSource())
        .withProcedureName("DYNAMIC_TABLE_CREATION_WITH_FIELDS");

    SqlParameterSource in = new MapSqlParameterSource()
        .addValue("tableNameWithFields", createTableSqlString);
    simpleJdbcCall.execute(in);
    log.info("Table Created Successfully.");
  }
}
