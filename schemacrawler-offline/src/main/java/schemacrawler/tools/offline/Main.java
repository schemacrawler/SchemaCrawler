/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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

package schemacrawler.tools.offline;


import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.commandline.ApplicationOptionsParser;
import schemacrawler.tools.commandline.CommandLine;
import schemacrawler.tools.commandline.SchemaCrawlerHelpCommandLine;
import schemacrawler.tools.options.ApplicationOptions;
import schemacrawler.tools.options.HelpOptions;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

  public static void main(final String[] args)
    throws Exception
  {
    String[] remainingArgs = args;

    final ApplicationOptionsParser applicationOptionsParser = new ApplicationOptionsParser();
    remainingArgs = applicationOptionsParser.parse(remainingArgs);
    final ApplicationOptions applicationOptions = applicationOptionsParser
      .getOptions();

    if (applicationOptions.isShowHelp())
    {
      final boolean showVersionOnly = applicationOptions.isShowVersionOnly();
      SchemaCrawlerHelpCommandLine helpCommandLine = new SchemaCrawlerHelpCommandLine(remainingArgs,
                                                                                      new HelpOptions("SchemaCrawler for Offline Snapshots",
                                                                                                      "/help/Offline.txt"),
                                                                                      showVersionOnly);
      helpCommandLine.execute();
      return;
    }

    applicationOptions.applyApplicationLogLevel();
    LOGGER.log(Level.CONFIG, "Command line: " + Arrays.toString(args));

    final CommandLine commandLine = new OfflineSnapshotCommandLine(remainingArgs);
    commandLine.execute();
  }

  private Main()
  {
    // Prevent instantiation
  }

}
