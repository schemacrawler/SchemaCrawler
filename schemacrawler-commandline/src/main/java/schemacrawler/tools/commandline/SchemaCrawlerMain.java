/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.options.ApplicationOptions;
import schemacrawler.tools.options.BundledDriverOptions;
import schemacrawler.tools.options.HelpOptions;

public class SchemaCrawlerMain
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerMain.class
    .getName());

  public static void main(final String[] args)
    throws Exception
  {
    main(args, new HelpOptions(), null);
  }

  public static void main(final String[] args,
                          final BundledDriverOptions bundledDriverOptions)
    throws Exception
  {
    if (bundledDriverOptions == null)
    {
      throw new IllegalArgumentException("No bundled driver options provided");
    }
    main(args,
         bundledDriverOptions.getHelpOptions(),
         bundledDriverOptions.getConfig());
  }

  public static void main(final String[] args,
                          final HelpOptions helpOptions,
                          final Config config)
    throws Exception
  {
    final CommandLine commandLine;
    final boolean showHelp;

    final ApplicationOptions applicationOptions;
    String[] remainingArgs = args;
    if (remainingArgs.length == 0)
    {
      applicationOptions = new ApplicationOptions();
      showHelp = true;
    }
    else
    {
      final ApplicationOptionsParser applicationOptionsParser = new ApplicationOptionsParser();
      remainingArgs = applicationOptionsParser.parse(remainingArgs);
      applicationOptions = applicationOptionsParser.getOptions();
      showHelp = applicationOptions.isShowHelp();
    }
    applicationOptions.applyApplicationLogLevel();
    LOGGER.log(Level.CONFIG, "Command line: " + Arrays.toString(args));

    if (showHelp)
    {
      final boolean showVersionOnly = applicationOptions.isShowVersionOnly();
      commandLine = new SchemaCrawlerHelpCommandLine(remainingArgs,
                                                     helpOptions,
                                                     showVersionOnly);
    }
    else
    {
      commandLine = new SchemaCrawlerCommandLine(config, remainingArgs);
    }
    commandLine.execute();
  }

  private SchemaCrawlerMain()
  {

  }

}
