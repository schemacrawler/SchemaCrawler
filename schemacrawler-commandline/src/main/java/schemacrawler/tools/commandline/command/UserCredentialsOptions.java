/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.command;


import static sf.util.Utility.isBlank;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.databaseconnector.SingleUseUserCredentials;
import schemacrawler.tools.databaseconnector.UserCredentials;

/**
 * Options for the command-line.
 *
 * @author sfatehi
 */
public final class UserCredentialsOptions
{

  @CommandLine.Option(names = "--password:env",
                      description = "Database password, from an environmental variable value")
  private String passwordEnvironmentVariable;
  @CommandLine.Option(names = "--password:file",
                      description = "Database password, read from a file")
  private File passwordFile;
  @CommandLine.Option(names = "--password:prompt",
                      interactive = true,
                      description = "Database password, prompted from the console")
  private String passwordPrompted;
  @CommandLine.Option(names = {
    "--password"
  },
                      description = "Database password")
  private String passwordProvided;
  @CommandLine.Option(names = {
    "--user"
  },
                      description = "Database user name")
  private String user;

  public UserCredentials getUserCredentials()
  {
    return new SingleUseUserCredentials(user, getPassword());
  }

  /**
   * Get password from various sources, in order of precedence.
   * The password cannot be specified in more than one way.
   *
   * @return Password, can be null
   */
  private String getPassword()
  {
    String password = getPasswordProvided();

    if (password == null)
    {
      password = getPasswordPrompted();
    }
    if (password == null)
    {
      password = getPasswordFromFile();
    }
    if (password == null)
    {
      password = getPasswordFromEnvironment();
    }

    return password;
  }

  private String getPasswordProvided()
  {
    // Check that password was not provided in any other way
    final boolean passwordInOtherWays =
      passwordFile != null || passwordPrompted != null || !isBlank(
        passwordEnvironmentVariable);

    if (passwordProvided != null && passwordInOtherWays)
    {
      throw new SchemaCrawlerRuntimeException(
        "Database password provided in too many ways");
    }

    return passwordProvided;
  }

  private String getPasswordPrompted()
  {
    // Check that password was not provided in any other way
    final boolean passwordInOtherWays =
      passwordFile != null || passwordProvided != null || !isBlank(
        passwordEnvironmentVariable);

    if (passwordPrompted != null && passwordInOtherWays)
    {
      throw new SchemaCrawlerRuntimeException(
        "Database password provided in too many ways");
    }

    return passwordPrompted;
  }

  private String getPasswordFromEnvironment()
  {
    if (isBlank(passwordEnvironmentVariable))
    {
      return null;
    }

    String password = null;
    try
    {
      password = System.getenv(passwordEnvironmentVariable);
    }
    catch (final Exception e)
    {
      throw new SchemaCrawlerRuntimeException("Cannot read password file", e);
    }

    // Check that password was not provided in any other way
    if (passwordFile != null || !isBlank(passwordPrompted) || !isBlank(
      passwordProvided))
    {
      throw new SchemaCrawlerRuntimeException(
        "Database password provided in too many ways");
    }

    return password;
  }

  private String getPasswordFromFile()
  {
    if (passwordFile == null)
    {
      return null;
    }

    String password = null;
    try
    {
      final List<String> lines = Files.readAllLines(passwordFile.toPath());
      if (!lines.isEmpty()) {password = lines.get(0);}
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerRuntimeException(
        "Cannot read database password file",
        e);
    }

    // Check that password was not provided in any other way
    if (!isBlank(passwordEnvironmentVariable) || !isBlank(passwordPrompted)
        || !isBlank(passwordProvided))
    {
      throw new SchemaCrawlerRuntimeException(
        "Database password provided in too many ways");
    }

    return password;
  }

}
