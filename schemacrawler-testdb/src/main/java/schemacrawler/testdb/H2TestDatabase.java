package schemacrawler.testdb;



public class H2TestDatabase extends BaseTestDatabase
{

  public static void main(final String[] args)
    throws Exception
  {
    new H2TestDatabase().testConnection();
  }

  public String getContext()
  {
    return "h2-context.xml";
  }

}
