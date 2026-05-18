/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.command;

import static java.nio.charset.StandardCharsets.UTF_8;
import static us.fatehi.utility.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import picocli.CommandLine.Option;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import us.fatehi.utility.ioresource.FileInputResource;

public final class UserOptions {

  @Option(
      names = "--user:env",
      description = "Database user name, from an environmental variable value",
      paramLabel = "<environment variable name>")
  private String userEnvironmentVariable;

  @Option(
      names = "--user:file",
      description = "Database user name, read from a file",
      paramLabel = "<path to user file>")
  private Path userFile;

  @Option(
      names = "--user:prompt",
      interactive = true,
      description = "Database user name, prompted from the console")
  private String userPrompted;

  @Option(
      names = {"--user"},
      description = "Database user name",
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
          "User could not be read from environmental variable <%s>"
              .formatted(userEnvironmentVariable),
          e);
    }

    return userEnvironment;
  }

  private String getUserFromFile() {
    if (userFile == null) {
      return null;
    }

    try {
      final FileInputResource inputResource = new FileInputResource(userFile);
      try (final BufferedReader reader = inputResource.openNewInputReader(UTF_8)) {
        return reader.readLine();
      }
    } catch (final IOException e) {
      throw new IORuntimeException("User could not be read from file <%s>".formatted(userFile), e);
    }
  }

  private String getUserPrompted() {
    return userPrompted;
  }

  private String getUserProvided() {
    return userProvided;
  }
}
