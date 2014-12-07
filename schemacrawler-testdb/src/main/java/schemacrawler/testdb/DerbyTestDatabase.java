package schemacrawler.testdb;



public class DerbyTestDatabase extends BaseTestDatabase
{

  public static void main(final String[] args)
    throws Exception
  {
    new DerbyTestDatabase().testConnection();
  }

  public String getContext()
  {
    return "derby-context.xml";
  }

}
