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


import java.io.Console;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.Version;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import sf.util.clparser.StringOption;

/**
 * Options for the command-line.
 *
 * @author sfatehi
 */
abstract class BaseDatabaseConnectionOptionsParser
  extends BaseConfigOptionsParser
{

  private static final Logger LOGGER = Logger
    .getLogger(BaseDatabaseConnectionOptionsParser.class.getName());

  private static final String USER = "user";
  private static final String PASSWORD = "password";

  BaseDatabaseConnectionOptionsParser(final Config config)
  {
    super(config, new StringOption('u', USER, null), new StringOption(PASSWORD,
                                                                      null));
  }

  @Override
  protected void loadConfig()
    throws SchemaCrawlerException
  {
    setUser();
    setPassword();
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

  private void setPassword()
  {
    final String password;
    if (hasOptionValue(PASSWORD))
    {
      password = getStringValue(PASSWORD);
    }
    else
    {
      password = promptForPassword();
    }
    config.put(PASSWORD, password);
  }

  private void setUser()
  {
    config.put(USER, getStringValue(USER));
  }

}
