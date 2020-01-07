package com.mdm.DynamicTable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import liquibase.CatalogAndSchema;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.ObjectDifferences;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.integration.commandline.CommandLineUtils;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import liquibase.structure.DatabaseObject;

public class GenerateDiffLog {

  Logger logger = Logger.getLogger(GenerateDiffLog.class.getName());


  public DiffResult diff(String paasTestDbUrl, String paasTestDbUser, String paasTestDbPassword,
      String paasTestDb2Url, String paasTestDb2User,
      String paasTestDb2Password)
      throws SQLException, LiquibaseException, IOException, ParserConfigurationException {

    logger
        .info("Running liquibase diff between db: " + paasTestDbUrl + " and db: " + paasTestDb2Url);
    Database referenceDatabase = null;
    Database targetDatabase = null;

    try {
      referenceDatabase = createDatabase(paasTestDbUrl, paasTestDbUser, paasTestDbPassword);
      targetDatabase = createDatabase(paasTestDb2Url, paasTestDb2User, paasTestDb2Password);

      final DiffGeneratorFactory generatorFactory = DiffGeneratorFactory.getInstance();
      final CompareControl compareControl = new CompareControl();

      final DiffResult diffResult = generatorFactory
          .compare(referenceDatabase, targetDatabase, compareControl);

      boolean ignoreDefaultValueDifference = false;
      if (ignoreDefaultValueDifference) {
        Map<DatabaseObject, ObjectDifferences> changedObjects = diffResult.getChangedObjects();
        for (DatabaseObject changedDbObject : changedObjects.keySet()) {
          ObjectDifferences objectDifferences = changedObjects.get(changedDbObject);
          if (objectDifferences.removeDifference("defaultValue")) {
            logger.info("Ignoring default value for {}" + changedDbObject.toString());
          }
          if (!objectDifferences.hasDifferences()) {
            logger.info("removing {}, no difference left." + changedDbObject.toString());
            changedObjects.remove(objectDifferences);
          }
        }
      }

      return diffResult;

    } finally {
      closeDatabase(referenceDatabase);
      closeDatabase(targetDatabase);
    }
  }


  private Database createDatabase(String dbUrl, String dbUser, String dbPassword)
      throws SQLException, LiquibaseException {
    Connection c = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    JdbcConnection liquibaseDbConnection = new JdbcConnection(c);

    Liquibase liquibase = new Liquibase(null, null, liquibaseDbConnection);

    return liquibase.getDatabase();
  }

  private void closeDatabase(Database db) {
    if (db != null) {
      try {
        logger.info("closing connection to:" + db);
        db.close();
      } catch (DatabaseException e) {
        logger.warning("unable to close database " + db + " exception:" + e.getMessage());
      }
    }

  }

  private static void getDiff(GenerateDiffLog diffLog)
      throws SQLException, LiquibaseException, IOException, ParserConfigurationException {
    DiffResult diffResult = diffLog
        .diff("jdbc:oracle:thin:@localhost:1521/ORCL", "springUser3", "letmein0",
            "jdbc:oracle:thin:@localhost:1521/ORCL", "springUser2", "letmein0");

    File file = new File(String.valueOf(ZonedDateTime.now().hashCode()));
    FileOutputStream fos = new FileOutputStream(file);
    diffResult.getMissingObjects().forEach(
        entry -> {
          System.out.println("Entry");
          try {
            fos.write(entry.getAttributes().toString().getBytes());
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
    );
    fos.close();
  }

  public void generateChangeLog(String dbUrl, String dbUser, String dbPassword,
      String changeLogFile)
      throws LiquibaseException, IOException, SQLException, ParserConfigurationException {
//    logger.info("Generating changelog from database {}@{} to {}"+ dbUser, dbUrl, changeLogFile);
    if (changeLogFile == null) {
      changeLogFile = ""; //will output to stdout
    }
//    removeExistingChangeLog(changeLogFile);

    Database db = createDatabase(dbUrl, dbUser, dbPassword);

    DiffOutputControl requireTablespaceForDiff = new DiffOutputControl(false, false, true,
        new CompareControl().getSchemaComparisons());

//    CatalogAndSchema[] defaultCatalogAndSchema = new CatalogAndSchema[]{CatalogAndSchema.DEFAULT};
    CatalogAndSchema[] defaultCatalogAndSchema = new CatalogAndSchema[1];
    defaultCatalogAndSchema[0] = new CatalogAndSchema(null,
        null); // this should be the next migrating db user so that database changes applied to that schema user.
    String requireAllTypesForSnapshot = null;
    try {
      CommandLineUtils.doGenerateChangeLog(changeLogFile, db, defaultCatalogAndSchema,
          requireAllTypesForSnapshot, "springUser2", null, null, requireTablespaceForDiff);

    } finally {
      closeDatabase(db);
    }

  }

  public void applyChangeLog(String dbUrl, String dbUser, String dbPassword, String changeLogFile)
      throws SQLException, LiquibaseException {
    logger.info("Applying changes in database " + dbUrl + " using " + changeLogFile);
    Database db = null;
    try {
      db = createDatabase(dbUrl, dbUser, dbPassword);
      ResourceAccessor resourceAccessor = null;
      if (changeLogFile.startsWith("classpath:")) {
        resourceAccessor = new ClassLoaderResourceAccessor();
        changeLogFile = changeLogFile.substring(10);
      } else {
        resourceAccessor = new FileSystemResourceAccessor(".");
      }

      Liquibase liquibase = new Liquibase(
          "C:\\MyProjects\\JavaPractice\\MDM\\master-data-service-rdbms\\src\\main\\resources\\db\\changelog\\changes\\dbchangelog12.xml",
          resourceAccessor, db);

      liquibase.update("test");
    } finally {
      logger.info("closing connection to:" + db);
      closeDatabase(db);
    }
  }


  public static void main(String a[])
      throws SQLException, ParserConfigurationException, LiquibaseException, IOException {
    GenerateDiffLog diffLog = new GenerateDiffLog();
    getDiff(diffLog);
//    diffLog.generateChangeLog("jdbc:oracle:thin:@localhost:1521/ORCL", "springUser3", "letmein0","C:\\MyProjects\\JavaPractice\\MDM\\master-data-service-rdbms\\src\\main\\resources\\db\\changelog\\changes\\dbchangelog12.xml");

//    diffLog.applyChangeLog("jdbc:oracle:thin:@localhost:1521/ORCL", "springUser2", "letmein0", "C:\\MyProjects\\JavaPractice\\MDM\\master-data-service-rdbms\\src\\main\\resources\\db\\changelog\\dbchangelog.xml");
  }


}
