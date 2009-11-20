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

package schemacrawler.tools.main.dbconnector;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.tools.main.BaseOptionsParser;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
abstract class BaseDatabaseConnectionOptionsParser
  extends BaseOptionsParser<DatabaseConnectionOptions>
{

  protected final StringOption optionUser = new StringOption(Option.NO_SHORT_FORM,
                                                             "user",
                                                             null);
  protected final StringOption optionPassword = new StringOption(Option.NO_SHORT_FORM,
                                                                 "password",
                                                                 "");

  protected final Config config;

  /**
   * Parses the command line into options.
   * 
   * @param args
   */
  protected BaseDatabaseConnectionOptionsParser(final String[] args,
                                                final Config config)
  {
    super(args);
    this.config = config;
  }

}
