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
package schemacrawler.tools.commandline;


import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.options.ApplicationOptions;
import schemacrawler.tools.options.BundledDriverOptions;

public class SchemaCrawlerMain
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerMain.class
    .getName());

  public static void main(final String[] args)
    throws Exception
  {
    main(args, new BundledDriverOptions()
    {

      private static final long serialVersionUID = 1352308748762219275L;
    });
  }

  public static void main(final String[] args,
                          final BundledDriverOptions bundledDriverOptions)
    throws Exception
  {
    if (bundledDriverOptions == null)
    {
      throw new IllegalArgumentException("No bundled driver options provided");
    }

    String[] remainingArgs = args;

    final ApplicationOptionsParser applicationOptionsParser = new ApplicationOptionsParser();
    remainingArgs = applicationOptionsParser.parse(remainingArgs);
    final ApplicationOptions applicationOptions = applicationOptionsParser
      .getOptions();

    if (applicationOptions.isShowHelp())
    {
      final boolean showVersionOnly = applicationOptions.isShowVersionOnly();
      final CommandLine helpCommandLine = new SchemaCrawlerHelpCommandLine(remainingArgs,
                                                                           bundledDriverOptions
                                                                             .getHelpOptions(),
                                                                           showVersionOnly);
      helpCommandLine.execute();
      return;
    }

    applicationOptions.applyApplicationLogLevel();
    LOGGER.log(Level.CONFIG, "Command line: " + Arrays.toString(args));

    final CommandLine commandLine = new SchemaCrawlerCommandLine(bundledDriverOptions,
                                                                 remainingArgs);
    commandLine.execute();
  }

  private SchemaCrawlerMain()
  {

  }

}
