package com.master.data.management.dao;

import java.util.List;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Repository
public class DataModelDAO {

  private static final String GET_ALL_TABLES_FROM_DATABASE = "select * from information_schema. tables where table_schema  like 'public'";
  private static final String TABLE_VERSIONS_COUNT = "select count(*) from information_schema. tables where table_schema  like 'public' and table_name = 'tableversions'";
  private String GET_ALL_FIELDS_FROM_TABLE = "SELECT * FROM information_schema.columns WHERE table_schema = 'public' AND table_name='%s'";

  @Autowired
  private JdbcTemplate jdbcTemplate;
  private boolean isTableVersionsExists;
  @Autowired
  private PlatformTransactionManager platformTransactionManager;


  @PostConstruct
  void init() {
    // o_name and O_NAME, same
    jdbcTemplate.setResultsMapCaseInsensitive(true);
    isTableVersionsExists = checkTableVersionsExists();
    if (!isTableVersionsExists) {
      jdbcTemplate.execute(
          "CREATE TABLE TableVersions (ID UUID, TABLE_NAME VARCHAR(20) PRIMARY KEY, VERSION numeric, TABLE_PAYLOAD VARCHAR(500), DELTA VARCHAR(2000))");
    }
  }

  private boolean checkTableVersionsExists() {
    return jdbcTemplate.queryForList(TABLE_VERSIONS_COUNT, Integer.class).get(0) > 0;
  }

  public void executeSQL(String createTableSqlString) {
    log.info("Executing SQL : {}", createTableSqlString);
    jdbcTemplate.execute(createTableSqlString);
    log.info("SQL Executed Successfully.");
  }

  public List<String> getTablesFromDatabase() {
    return jdbcTemplate.queryForList(GET_ALL_TABLES_FROM_DATABASE, String.class);
  }

  public List<String> getFieldNamesByTableName(String tableName) {
    return jdbcTemplate
        .queryForList(String.format(GET_ALL_FIELDS_FROM_TABLE, tableName), String.class);
  }

}

