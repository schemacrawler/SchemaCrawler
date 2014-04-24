package schemacrawler.tools.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;

public interface CommandProvider
{

  String getCommand();

  String getHelpResource();

  Executable configureNewExecutable(SchemaCrawlerOptions schemaCrawlerOptions,
                                    OutputOptions outputOptions)
    throws SchemaCrawlerException;

}
