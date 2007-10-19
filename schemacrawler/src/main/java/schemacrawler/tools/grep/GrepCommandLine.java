package schemacrawler.tools.grep;


import schemacrawler.crawl.SchemaCrawlerException;
import schemacrawler.main.SchemaCrawlerCommandLine;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public class GrepCommandLine
  extends SchemaCrawlerCommandLine
{

  private final GrepOptions grepOptions;

  /**
   * Loads objects from command line options.
   * 
   * @param args
   *        Command line arguments.
   * @throws SchemaCrawlerException
   */
  public GrepCommandLine(final String[] args)
    throws SchemaCrawlerException
  {
    this(args, null);
  }

  /**
   * Loads objects from command line options. Optionally loads the
   * config from the classpath.
   * 
   * @param args
   *        Command line arguments.
   * @param configResource
   *        Config resource.
   * @throws SchemaCrawlerException
   */
  public GrepCommandLine(final String[] args, final String configResource)
    throws SchemaCrawlerException
  {
    super(args, configResource);

    if (args != null && args.length > 0)
    {
      grepOptions = new GrepOptionsParser(args).getValue();
    }
    else
    {
      grepOptions = new GrepOptions();
    }
  }

  /**
   * Gets the grep options.
   * 
   * @return Grep options.
   */
  public GrepOptions getGrepOptions()
  {
    return grepOptions;
  }

}
