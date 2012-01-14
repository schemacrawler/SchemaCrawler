package schemacrawler.schemacrawler;


import java.sql.SQLException;

public class SchemaCrawlerSQLException
  extends SQLException
{

  private static final long serialVersionUID = 3424948223257267142L;

  public SchemaCrawlerSQLException(String reason, SQLException cause)
  {
    super(reason + ": " + cause.getMessage(), cause);
  }

}
