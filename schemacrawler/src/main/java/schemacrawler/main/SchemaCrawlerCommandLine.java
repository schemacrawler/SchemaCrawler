package schemacrawler.main;


import java.util.Arrays;

import schemacrawler.tools.Command;
import schemacrawler.tools.OutputOptions;
import sf.util.Config;
import sf.util.Utilities;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public class SchemaCrawlerCommandLine
{

  private final String[] args;
  //
  private final Command[] commands;
  private final Config config;
  private final OutputOptions outputOptions;

  /**
   * Loads objects from command line options.
   * 
   * @param args
   *        Command line arguments.
   */
  public SchemaCrawlerCommandLine(final String[] args)
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
   */
  public SchemaCrawlerCommandLine(final String[] args, final String configResource)
  {
    this.args = args;
    if (args != null && args.length > 0)
    {
      commands = new CommandParser(args).getValue();
      outputOptions = new OutputOptionsParser(args).getValue();
    }
    else
    {
      commands = new Command[0];
      outputOptions = new OutputOptions();
    }

    if (!Utilities.isBlank(configResource))
    {
      config = Config.load(SchemaCrawlerCommandLine.class
        .getResourceAsStream(configResource));
    }
    else
    {
      if (args != null && args.length > 0)
      {
        config = new ConfigParser(args).getValue();
      }
      else
      {
        config = new Config();
      }
    }
  }

  /**
   * Gets the commands.
   * 
   * @return Commands.
   */
  public Command[] getCommands()
  {
    return Arrays.asList(commands).toArray(new Command[commands.length]);
  }

  /**
   * Gets the config.
   * 
   * @return Config.
   */
  public Config getConfig()
  {
    return new Config(config);
  }

  /**
   * Gets the output options.
   * 
   * @return Output options.
   */
  public OutputOptions getOutputOptions()
  {
    return outputOptions.duplicate();
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return Arrays.toString(args);
  }

}
