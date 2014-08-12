package schemacrawler.tools.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;

public interface CommandProvider
{

  Executable configureNewExecutable(SchemaCrawlerOptions schemaCrawlerOptions,
                                    OutputOptions outputOptions)
    throws SchemaCrawlerException;

  String getCommand();

  String getHelpResource();

}
