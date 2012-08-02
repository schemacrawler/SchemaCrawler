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


import java.io.Console;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.Version;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import sf.util.clparser.StringOption;

/**
 * Options for the command line.
 * 
 * @author sfatehi
 */
abstract class BaseDatabaseConnectionOptionsParser
  extends BaseOptionsParser<ConnectionOptions>
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseDatabaseConnectionOptionsParser.class.getName());

  protected final Config config;

  BaseDatabaseConnectionOptionsParser(final Config config)
  {
    super(new StringOption('u', "user", null), new StringOption("password",
                                                                null));
    this.config = config;
  }

  protected final void setPassword(final ConnectionOptions connectionOptions)
  {
    final String password;
    if (hasOptionValue("password"))
    {
      password = getStringValue("password");
    }
    else
    {
      password = promptForPassword();
    }
    connectionOptions.setPassword(password);
  }

  protected final void setUser(final ConnectionOptions connectionOptions)
  {
    connectionOptions.setUser(getStringValue("user"));
  }

  private String promptForPassword()
  {
    final Console console = System.console();
    if (console == null)
    {
      LOGGER.log(Level.WARNING, "System console is not available");
      return null;
    }

    try
    {
      console.format(Version.about());
      final char[] passwordChars = console.readPassword("password: ");
      return new String(passwordChars);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "System console is not available", e);
      return null;
    }
  }

}
