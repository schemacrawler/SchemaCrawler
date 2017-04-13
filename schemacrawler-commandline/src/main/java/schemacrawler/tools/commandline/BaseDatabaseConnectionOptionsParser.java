/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.Version;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SingleUseUserCredentials;
import schemacrawler.schemacrawler.UserCredentials;
import sf.util.SchemaCrawlerLogger;

/**
 * Options for the command-line.
 *
 * @author sfatehi
 */
abstract class BaseDatabaseConnectionOptionsParser
  extends BaseConfigOptionsParser
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(BaseDatabaseConnectionOptionsParser.class.getName());

  private static final String USER = "user";
  private static final String PASSWORD = "password";

  private UserCredentials userCredentials;

  BaseDatabaseConnectionOptionsParser(final Config config)
  {
    super(config);
    normalizeOptionName(USER, "u");
    normalizeOptionName(PASSWORD);
  }

  public final UserCredentials getUserCredentials()
  {
    return userCredentials;
  }

  @Override
  public void loadConfig()
    throws SchemaCrawlerException
  {
    // Get the database username and password, and remove them from the
    // config
    userCredentials = new SingleUseUserCredentials(getUser(), getPassword());
  }

  private String getPassword()
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
    config.remove(PASSWORD);
    return password;
  }

  private String getUser()
  {
    final String user = config.getStringValue(USER, null);
    config.remove(USER);
    return user;
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
