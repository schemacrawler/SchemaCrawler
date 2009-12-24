package schemacrawler.tools;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public interface ExecutableOptions
  extends Options
{

  String getCommand();

  Config getConfig();

  ConnectionOptions getConnectionOptions();

  OutputOptions getOutputOptions();

  SchemaCrawlerOptions getSchemaCrawlerOptions();

}
