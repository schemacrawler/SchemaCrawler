package schemacrawler.tools;


import java.sql.Connection;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public interface Executable
{

  /**
   * Executes with the command line, and a given executor. The executor
   * allows for the command line to be parsed independently of the
   * execution. The execution can integrate with other software, such as
   * Velocity.
   * 
   * @param commandLine
   *        Command line arguments
   * @throws Exception
   *         On an exception
   */
  void execute()
    throws Exception;

  /**
   * Executes main functionality for SchemaCrawler.
   * 
   * @param connection
   *        Database connection
   * @throws Exception
   *         On an exception
   */
  void execute(Connection connection)
    throws ExecutionException;

  /**
   * Executes main functionality for SchemaCrawler.
   * 
   * @param dataSource
   *        Data-source
   * @throws Exception
   *         On an exception
   */
  void execute(final DataSource dataSource)
    throws ExecutionException;

  String getCommand();

  Config getConfig();

  ConnectionOptions getConnectionOptions();

  OutputOptions getOutputOptions();

  SchemaCrawlerOptions getSchemaCrawlerOptions();

  void setCommand(final String command);

  void setConfig(final Config config);

  void setConnectionOptions(final ConnectionOptions connectionOptions);

  void setExecutableOptions(final ExecutableOptions executableOptions);

  void setOutputOptions(final OutputOptions outputOptions);

  void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions);

}
