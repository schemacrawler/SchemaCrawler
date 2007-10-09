package schemacrawler.main;


import java.util.List;

import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.tools.Command;
import schemacrawler.tools.OutputOptions;
import sf.util.Config;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public class CommandLineParser
{
  /**
   * Parses the command line.
   * 
   * @param args
   *        Command line arguments
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  public static List<Command> parseCommands(final String[] args)
    throws SchemaCrawlerException
  {
    return CommandParser.parseCommands(args);
  }

  /**
   * Parses the command line.
   * 
   * @param args
   *        Command line arguments
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  public static Config parseConfig(final String[] args)
    throws SchemaCrawlerException
  {
    return ConfigParser.parseConfig(args);
  }

  /**
   * Parses the command line.
   * 
   * @param args
   *        Command line arguments
   * @return Command line options
   * @throws SchemaCrawlerException
   */
  public static OutputOptions parseOutputOptions(final String[] args)
    throws SchemaCrawlerException
  {
    return OutputOptionsParser.parseOutputOptions(args);
  }

}
