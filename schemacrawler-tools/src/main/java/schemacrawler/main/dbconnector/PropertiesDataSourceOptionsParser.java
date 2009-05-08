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

package schemacrawler.main.dbconnector;


import sf.util.CommandLineParser.BooleanOption;
import sf.util.CommandLineParser.Option;
import sf.util.CommandLineParser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
final class PropertiesDataSourceOptionsParser
  extends BaseConnectorOptionsParser<PropertiesDataSourceOptions>
{

  private final BooleanOption optionUseDefaultConnection = new BooleanOption('d',
                                                                             "default");
  private final StringOption optionConnection = new StringOption('c',
                                                                 "connection",
                                                                 null);
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
  public PropertiesDataSourceOptionsParser(final String[] args)
  {
    super(args);
  }

  @Override
  protected String getHelpResource()
  {
    return "/help/Commands.readme.txt";
  }

  @Override
  protected PropertiesDataSourceOptions getOptions()
  {
    parse(new Option[] {
        optionUseDefaultConnection,
        optionConnection,
        optionDriver,
        optionConnectionUrl,
        optionSchemaPattern,
        optionUser,
        optionPassword,
    });

    final PropertiesDataSourceOptions options = new PropertiesDataSourceOptions();
    options.setUseDefaultConnection(optionUseDefaultConnection.getValue());
    options.setConnection(optionConnection.getValue());
    options.setConnectionUrl(optionConnectionUrl.getValue());
    options.setDriver(optionDriver.getValue());
    options.setSchemapattern(optionSchemaPattern.getValue());
    options.setUser(optionUser.getValue());
    options.setPassword(optionPassword.getValue());

    return options;
  }

}
