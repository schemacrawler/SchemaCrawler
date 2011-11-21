package schemacrawler.crawl;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.experimental.theories.DataPoint;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Privilege.Grant;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.util.ObjectEqualsHashCode;
import schemacrawler.utility.TestDatabase;

public class SchemaEqualsHashCodeTest
  extends ObjectEqualsHashCode
{

  @DataPoint
  public static Database database;
  @DataPoint
  public static Database dbNull = null;
  @DataPoint
  public static Database dbEmpty = new MutableDatabase("dbEmpty");

  @DataPoint
  public static Schema schema;
  @DataPoint
  public static Schema schemaNull = null;
  @DataPoint
  public static Schema schemaEmpty = new MutableSchema();

  @DataPoint
  public static SchemaReference schemaRef;
  @DataPoint
  public static SchemaReference schemaRefNull = null;
  @DataPoint
  public static SchemaReference schemaRef1 = new SchemaReference("catalog",
                                                                 "schema");
  @DataPoint
  public static SchemaReference schemaRef2 = new SchemaReference(null, "schema");
  @DataPoint
  public static SchemaReference schemaRef3 = new SchemaReference("catalog",
                                                                 null);

  @DataPoint
  public static Table table;
  @DataPoint
  public static Table tableNull = null;
  @DataPoint
  public static Table tableEmpty = new MutableTable(schemaEmpty, "tableEmpty");

  @DataPoint
  public static ForeignKey foreignKey;
  @DataPoint
  public static ForeignKey foreignKeyNull = null;
  @DataPoint
  public static ForeignKey foreignKeyEmpty = new MutableForeignKey("foreignKeyEmpty");

  @DataPoint
  public static PrimaryKey primaryKey;
  @DataPoint
  public static PrimaryKey primaryKeyNull = null;
  @DataPoint
  public static PrimaryKey primaryKeyEmpty = new MutablePrimaryKey(new MutableIndex(tableEmpty,
                                                                                    "primaryKeyEmpty"));
  @DataPoint
  public static Column column;
  @DataPoint
  public static Column columnNull = null;
  @DataPoint
  public static Column columnEmpty = new MutableColumn(tableEmpty,
                                                       "columnEmpty");

  @DataPoint
  public static ForeignKeyColumnMap fkColumnPair;
  @DataPoint
  public static ForeignKeyColumnMap fkColumnPairNull = null;
  @DataPoint
  public static ForeignKeyColumnMap fkColumnPairEmpty = new MutableForeignKeyColumnMap();

  @DataPoint
  public static JavaSqlType LONGNVARCHAR = JavaSqlTypesUtility
    .lookupSqlDataType(-16);
  @DataPoint
  public static JavaSqlType NCLOB = JavaSqlTypesUtility
    .lookupSqlDataType("NCLOB");
  @DataPoint
  public static JavaSqlType javaSqlTypeNull = JavaSqlTypesUtility
    .lookupSqlDataType("TEST");

  @DataPoint
  public static Privilege privilege;
  @DataPoint
  public static Privilege privilegeNull = null;
  @DataPoint
  public static Privilege privilegeEmpty = new MutablePrivilege(tableEmpty,
                                                                "privilegeEmpty");

  @DataPoint
  public static Grant grant;
  @DataPoint
  public static Grant grantNull = null;
  @DataPoint
  public static Grant grantEmpty = new Grant()
  {

    private static final long serialVersionUID = 1440765929570312732L;

    @Override
    public String getGrantee()
    {
      return "grantEmpty-Grantee";
    }

    @Override
    public String getGrantor()
    {
      return "grantEmpty-Grantor";
    }

    @Override
    public boolean isGrantable()
    {
      return false;
    }
  };

  static
  {
    final TestDatabase testDatabase = new TestDatabase();
    try
    {
      TestDatabase.initializeApplicationLogging();
      testDatabase.startMemoryDatabase();
      final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
      schemaCrawlerOptions.setSchemaInfoLevel(SchemaInfoLevel.maximum());
      database = testDatabase.getDatabase(schemaCrawlerOptions);
      final Schema[] schemas = database.getSchemas();
      assertTrue("No schemas found", schemas.length > 0);
      schema = schemas[0];
      final Table[] tables = schema.getTables();
      assertTrue("No tables found", tables.length > 0);
      table = tables[0];
      primaryKey = table.getPrimaryKey();
      final Column[] columns = table.getColumns();
      assertTrue("No columns found", columns.length > 0);
      column = columns[0];
      final ForeignKey[] foreignKeys = table.getForeignKeys();
      assertTrue("No foreign keys found", foreignKeys.length > 0);
      foreignKey = foreignKeys[0];
      final ForeignKeyColumnMap[] fkColumnPairs = foreignKey.getColumnPairs();
      assertTrue("No foreign keys column pairs found", fkColumnPairs.length > 0);
      fkColumnPair = fkColumnPairs[0];
      final Privilege[] privileges = table.getPrivileges();
      assertTrue("No privileges found", privileges.length > 0);
      privilege = privileges[0];
      final Grant[] grants = privilege.getGrants();
      assertTrue("No grants found", grants.length > 0);
      grant = grants[0];
    }
    catch (final SchemaCrawlerException e)
    {
      fail("Could not initialize database: " + e);
    }
    finally
    {
      testDatabase.shutdownDatabase();
    }
  }

}
