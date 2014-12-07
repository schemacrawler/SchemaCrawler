package schemacrawler.testdb;


import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDatabase
{

  public static void main(final String[] args)
    throws Exception
  {
    final String server;
    if (args == null || args.length == 0)
    {
      server = "hsqldb";
    }
    else
    {
      server = args[0];
    }
    new TestDatabase(server).testConnection();
  }

  private final DataSource dataSource;

  public TestDatabase(final String server)
  {
    final ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
      getContext(server)
    });
    dataSource = context.getBean("dataSource", DataSource.class);
  }

  public final DataSource getDataSource()
  {
    return dataSource;
  }

  public final void testConnection()
    throws Exception
  {
    final Connection connection = dataSource.getConnection();
    System.out.println(String.format("%s %s", connection.getMetaData()
      .getDatabaseProductName(), connection.getMetaData()
      .getDatabaseProductVersion()));
  }

  private String getContext(final String server)
  {
    if (server == null || server.trim().isEmpty())
    {
      throw new IllegalArgumentException("No server provided");
    }
    return String.format("%s-context.xml", server);
  }

}
