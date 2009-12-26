package schemacrawler.tools.executable;


import java.sql.Connection;

import javax.sql.DataSource;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.options.OutputOptions;

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
  void execute(DataSource dataSource)
    throws ExecutionException;

  String getCommand();

  Config getConfig();

  ConnectionOptions getConnectionOptions();

  OutputOptions getOutputOptions();

  SchemaCrawlerOptions getSchemaCrawlerOptions();

  void setCommand(String command);

  void setConfig(Config config);

  void setConnectionOptions(ConnectionOptions connectionOptions);

  void setOutputOptions(OutputOptions outputOptions);

  void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions);

  void setSchemaInfoLevel(SchemaInfoLevel infoLevel);

}
