/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.commandline;


import java.io.Console;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.Version;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;

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
    super(config);
    normalizeOptionName(USER, "u");
    normalizeOptionName(PASSWORD);
  }

  @Override
  public void loadConfig()
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
    if (config.hasValue(PASSWORD))
    {
      password = config.getStringValue(PASSWORD, null);
    }
    else
    {
      password = promptForPassword();
    }
    config.put(PASSWORD, password);
  }

  private void setUser()
  {
    config.put(USER, config.getStringValue(USER, null));
  }

}
