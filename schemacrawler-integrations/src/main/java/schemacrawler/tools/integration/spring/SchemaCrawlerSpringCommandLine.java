package schemacrawler.tools.integration.spring;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.executable.Executable;
import sf.util.ObjectToString;

public class SchemaCrawlerSpringCommandLine
  implements CommandLine
{

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerSpringCommandLine.class.getName());

  private final SpringOptions springOptions;

  public SchemaCrawlerSpringCommandLine(final String[] args)
    throws SchemaCrawlerException
  {
    final SpringOptionsParser springOptionsParser = new SpringOptionsParser(args);
    springOptions = springOptionsParser.getOptions();
    final String[] remainingArgs = springOptionsParser.getUnparsedArgs();

    if (remainingArgs.length > 0)
    {
      throw new SchemaCrawlerException("Too many command line arguments provided: "
                                       + ObjectToString.toString(args));
    }
  }

  public void execute()
    throws Exception
  {
    Connection connection = null;
    try
    {
      final ApplicationContext appContext = new FileSystemXmlApplicationContext(springOptions
        .getContextFileName());

      final DataSource dataSource = (DataSource) appContext
        .getBean(springOptions.getDataSourceName());
      connection = dataSource.getConnection();
      LOGGER.log(Level.INFO, "Opened database connection, " + connection);

      final Executable executable = (Executable) appContext
        .getBean(springOptions.getExecutableName());
      executable.execute(connection);
    }
    finally
    {
      try
      {
        if (connection != null)
        {
          connection.close();
          LOGGER.log(Level.INFO, "Closed database connection, " + connection);
        }
      }
      catch (final SQLException e)
      {
        final String errorMessage = e.getMessage();
        LOGGER.log(Level.WARNING, "Could not close the connection: "
                                  + errorMessage);
      }
    }
  }

}
