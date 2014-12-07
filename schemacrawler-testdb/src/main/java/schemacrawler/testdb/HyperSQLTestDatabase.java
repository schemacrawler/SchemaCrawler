package schemacrawler.testdb;



public class HyperSQLTestDatabase extends BaseTestDatabase
{

  public static void main(final String[] args)
    throws Exception
  {
    new HyperSQLTestDatabase().testConnection();
  }

  public String getContext()
  {
    return "hsqldb-context.xml";
  }

}
