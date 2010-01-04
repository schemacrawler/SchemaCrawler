package schemacrawler.tools.executable;


import java.sql.Connection;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;

public interface Executable
{

  /**
   * Executes main functionality for SchemaCrawler.
   * 
   * @param connection
   *        Database connection
   * @throws Exception
   *         On an exception
   */
  void execute(Connection connection)
    throws Exception;

  Config getAdditionalConfiguration();

  String getCommand();

  OutputOptions getOutputOptions();

  SchemaCrawlerOptions getSchemaCrawlerOptions();

  void setAdditionalConfiguration(Config config);

  void setOutputOptions(OutputOptions outputOptions);

  void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions);

}
