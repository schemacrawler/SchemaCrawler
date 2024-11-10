/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static us.fatehi.utility.Utility.isBlank;
import picocli.CommandLine.Option;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;

public final class UserOptions {

  @Option(
      names = "--user:env",
      description = "Database username, from an environmental variable value",
      paramLabel = "<environment variable name>")
  private String userEnvironmentVariable;

  @Option(
      names = "--user:file",
      description = "Database username, read from a file",
      paramLabel = "<path to user file>")
  private Path userFile;

  @Option(
      names = "--user:prompt",
      interactive = true,
      description = "Database username, prompted from the console")
  private String userPrompted;

  @Option(
      names = {"--user"},
      description = "Database username",
      paramLabel = "<user>")
  private String userProvided;

  /**
   * Get user from various sources, in order of precedence. The user cannot be specified in more
   * than one way.
   *
   * @return User, can be null
   */
  String getUser() {
    String user = getUserProvided();

    if (user == null) {
      user = getUserPrompted();
    }
    if (user == null) {
      user = getUserFromFile();
    }
    if (user == null) {
      user = getUserFromEnvironment();
    }

    return user;
  }

  private String getUserFromEnvironment() {
    if (isBlank(userEnvironmentVariable)) {
      return null;
    }

    String userEnvironment = null;
    try {
      userEnvironment = System.getenv(userEnvironmentVariable);
    } catch (final Exception e) {
      throw new IllegalArgumentException(
          String.format(
              "User could not be read from environmental variable <%s>", userEnvironmentVariable),
          e);
    }

    return userEnvironment;
  }

  private String getUserFromFile() {
    if (userFile == null) {
      return null;
    }

    String user = null;
    try {
      final List<String> lines = Files.readAllLines(userFile);
      if (!lines.isEmpty()) {
        user = lines.get(0);
      }
    } catch (final IOException e) {
      throw new IORuntimeException(
          String.format("User could not be read from file <%s>", userFile), e);
    }

    return user;
  }

  private String getUserPrompted() {
    return userPrompted;
  }

  private String getUserProvided() {
    return userProvided;
  }
}
