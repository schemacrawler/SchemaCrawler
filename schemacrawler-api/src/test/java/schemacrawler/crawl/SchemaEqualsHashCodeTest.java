package schemacrawler.crawl;


import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Ignore;
import org.junit.experimental.theories.DataPoint;

import schemacrawler.schema.Column;
import schemacrawler.schema.Database;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Privilege.Grant;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.test.utility.TestDatabase;
import schemacrawler.util.ObjectEqualsHashCode;

@Ignore("Slow test")
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
  public static Schema schemaEmpty = new SchemaReference();

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
  public static Column column;
  @DataPoint
  public static Column columnNull = null;

  @DataPoint
  public static ForeignKeyColumnReference fkColumnReference;
  @DataPoint
  public static ForeignKeyColumnReference fkColumnReferenceNull = null;
  @DataPoint
  public static ForeignKeyColumnReference fkColumnReferenceEmpty = new MutableForeignKeyColumnReference();

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
  public static Grant grant;
  @DataPoint
  public static Grant grantNull = null;
  @DataPoint
  public static Grant grantEmpty = new Grant()
  {

    private static final long serialVersionUID = 1440765929570312732L;

    @Override
    public int compareTo(final Grant o)
    {
      return 0;
    }

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
      final Schema[] schemas = (Schema[]) database.getSchemas().toArray();
      assertTrue("No schemas found", schemas.length > 0);
      schema = schemas[0];
      final Table[] tables = database.getTables(schema).toArray(new Table[0]);
      assertTrue("No tables found", tables.length > 0);
      table = tables[0];
      primaryKey = table.getPrimaryKey();
      final Column[] columns = table.getColumns().toArray(new Column[0]);
      assertTrue("No columns found", columns.length > 0);
      column = columns[0];
      final ForeignKey[] foreignKeys = table.getForeignKeys()
        .toArray(new ForeignKey[0]);
      assertTrue("No foreign keys found", foreignKeys.length > 0);
      foreignKey = foreignKeys[0];
      final List<ForeignKeyColumnReference> fkColumnReferences = foreignKey
        .getColumnReferences();
      assertTrue("No foreign keys column references found",
                 fkColumnReferences.size() > 0);
      fkColumnReference = fkColumnReferences.get(0);
      final Privilege<Table>[] privileges = table.getPrivileges()
        .toArray(new Privilege[0]);
      assertTrue("No privileges found", privileges.length > 0);
      privilege = privileges[0];
      final Grant[] grants = (Grant[]) privilege.getGrants().toArray();
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
