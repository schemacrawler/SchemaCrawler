/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.commandline;


import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.ApplicationOptions;
import schemacrawler.tools.options.Command;
import schemacrawler.tools.options.HelpOptions;
import sf.util.Utility;

/**
 * Utility for parsing the SchemaCrawler command line.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerHelpCommandLine
{

  private static final long serialVersionUID = -3748989545708155963L;

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerHelpCommandLine.class.getName());

  private final boolean isShowHelp;
  private final boolean hideConfig;
  private final Command command;
  private final HelpOptions helpOptions;

  /**
   * Loads objects from command line options.
   * 
   * @param args
   *        Command line arguments.
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public SchemaCrawlerHelpCommandLine(final String[] args)
    throws SchemaCrawlerException
  {
    this(args, new HelpOptions(""), null);
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
   *         On an exception
   */
  public SchemaCrawlerHelpCommandLine(final String[] args,
                                      final HelpOptions helpOptions,
                                      final String configResource)
    throws SchemaCrawlerException
  {
    if (args == null)
    {
      throw new IllegalArgumentException("No command line arguments provided");
    }

    if (helpOptions == null)
    {
      throw new SchemaCrawlerException("No help options provided");
    }
    this.helpOptions = helpOptions;

    hideConfig = !Utility.isBlank(configResource);

    if (args.length == 0)
    {
      command = null;
      isShowHelp = true;
    }
    else
    {
      final ApplicationOptions applicationOptions = new ApplicationOptionsParser(args)
        .getOptions();
      isShowHelp = applicationOptions.isShowHelp();
      command = new CommandParser(args).getOptions();

      applicationOptions.applyApplicationLogLevel();
      LOGGER.log(Level.INFO, HelpOptions.about());
      LOGGER.log(Level.CONFIG, "Command line: " + Arrays.toString(args));
    }
  }

  public void execute()
    throws SchemaCrawlerException
  {
    if (isShowHelp)
    {
      showHelp();
      System.exit(0);
    }
  }

  public final boolean isShowHelp()
  {
    return isShowHelp;
  }

  public final String getCommand()
  {
    if (command != null)
    {
      return command.toString();
    }
    else
    {
      return null;
    }
  }

  private void showHelp()
  {
    System.out.println(helpOptions.getTitle());
    showHelp("/help/SchemaCrawler.txt");
    System.out.println();

    showHelp(helpOptions.getResourceConnections());
    showHelp("/help/SchemaCrawlerOptions.txt");
    if (!hideConfig)
    {
      showHelp("/help/Config.txt");
    }
    showHelp("/help/ApplicationOptions.txt");
  }

  private void showHelp(final String helpResource)
  {
    if (sf.util.Utility.isBlank(helpResource))
    {
      return;
    }
    final String helpText = Utility
      .readFully(SchemaCrawlerHelpCommandLine.class
        .getResourceAsStream(helpResource));
    System.out.println(helpText);
  }

}
