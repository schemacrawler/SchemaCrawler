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

package schemacrawler.tools.main;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.Utility;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
public final class CommandLineConnectionOptionsParser
  extends BaseDatabaseConnectionOptionsParser
{

  private final StringOption optionDriver = new StringOption(Option.NO_SHORT_FORM,
                                                             "driver",
                                                             null);
  private final StringOption optionConnectionUrl = new StringOption(Option.NO_SHORT_FORM,
                                                                    "url",
                                                                    null);

  /**
   * Parses the command line into options.
   * 
   * @param args
   */
  public CommandLineConnectionOptionsParser(final String[] args,
                                            final Config config)
  {
    super(args, config);
  }

  @Override
  public DatabaseConnectionOptions getOptions()
    throws SchemaCrawlerException
  {
    parse(new Option[] {
        optionDriver, optionConnectionUrl, optionUser, optionPassword,
    });

    final DatabaseConnectionOptions conenctionOptions;
    if (optionDriver.isFound() && optionConnectionUrl.isFound())
    {
      final String jdbcDriverClassName = optionDriver.getValue();
      final String connectionUrl = optionConnectionUrl.getValue();
      if (Utility.isBlank(jdbcDriverClassName)
          || Utility.isBlank(connectionUrl))
      {
        conenctionOptions = null;
      }
      else
      {
        conenctionOptions = new DatabaseConnectionOptions(jdbcDriverClassName,
                                                          connectionUrl);
        conenctionOptions.setUser(optionUser.getValue());
        conenctionOptions.setPassword(optionPassword.getValue());
      }
    }
    else
    {
      conenctionOptions = null;
    }
    return conenctionOptions;
  }

  @Override
  protected String getHelpResource()
  {
    return "/help/Commands.readme.txt";
  }

}
