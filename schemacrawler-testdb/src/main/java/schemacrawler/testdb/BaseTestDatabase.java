package schemacrawler.testdb;


import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

abstract class BaseTestDatabase
{

  private final DataSource dataSource;

  public BaseTestDatabase()
  {
    final ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
      getContext()
    });
    dataSource = context.getBean("dataSource", DataSource.class);
  }

  protected abstract String getContext();

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

}
