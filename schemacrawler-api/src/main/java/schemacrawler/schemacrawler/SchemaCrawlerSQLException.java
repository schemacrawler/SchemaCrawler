package schemacrawler.schemacrawler;


import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Iterator;

public class SchemaCrawlerSQLException
  extends SQLException
{

  private static final long serialVersionUID = 3424948223257267142L;
  private final String context;
  private final SQLException sqlEx;

  public SchemaCrawlerSQLException(final String context,
                                   final SQLException sqlEx)
  {
    this.context = context;
    this.sqlEx = sqlEx;
  }

  @Override
  public boolean equals(final Object obj)
  {
    return sqlEx.equals(obj);
  }

  @Override
  public Throwable getCause()
  {
    return sqlEx.getCause();
  }

  @Override
  public int getErrorCode()
  {
    return sqlEx.getErrorCode();
  }

  @Override
  public String getLocalizedMessage()
  {
    return context + ": " + sqlEx.getLocalizedMessage();
  }

  @Override
  public String getMessage()
  {
    return context + ": " + sqlEx.getMessage();
  }

  @Override
  public SQLException getNextException()
  {
    return sqlEx.getNextException();
  }

  @Override
  public String getSQLState()
  {
    return sqlEx.getSQLState();
  }

  @Override
  public StackTraceElement[] getStackTrace()
  {
    return sqlEx.getStackTrace();
  }

  @Override
  public int hashCode()
  {
    return sqlEx.hashCode();
  }

  @Override
  public Iterator<Throwable> iterator()
  {
    return sqlEx.iterator();
  }

  @Override
  public void printStackTrace()
  {
    sqlEx.printStackTrace();
  }

  @Override
  public void printStackTrace(final PrintStream s)
  {
    sqlEx.printStackTrace(s);
  }

  @Override
  public void printStackTrace(final PrintWriter s)
  {
    sqlEx.printStackTrace(s);
  }

  @Override
  public void setNextException(final SQLException ex)
  {
    sqlEx.setNextException(ex);
  }

  @Override
  public String toString()
  {
    return sqlEx.toString();
  }

}
