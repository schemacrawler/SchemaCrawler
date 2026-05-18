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
import java.nio.file.Files;
import java.nio.file.Path;
import picocli.CommandLine.Option;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;

public final class PasswordOptions {

  @Option(
      names = "--password:env",
      description = "Database password, from an environmental variable value",
      paramLabel = "<environment variable name>")
  private String passwordEnvironmentVariable;

  @Option(
      names = "--password:file",
      description = "Database password, read from a file",
      paramLabel = "<path to password file>")
  private Path passwordFile;

  @Option(
      names = "--password:prompt",
      interactive = true,
      description = "Database password, prompted from the console")
  private String passwordPrompted;

  @Option(
      names = {"--password"},
      description = "Database password",
      paramLabel = "<password>")
  private String passwordProvided;

  /**
   * Get password from various sources, in order of precedence. The password cannot be specified in
   * more than one way.
   *
   * @return Password, can be null
   */
  String getPassword() {
    String password = getPasswordProvided();

    if (password == null) {
      password = getPasswordPrompted();
    }
    if (password == null) {
      password = getPasswordFromFile();
    }
    if (password == null) {
      password = getPasswordFromEnvironment();
    }

    return password;
  }

  private String getPasswordFromEnvironment() {
    if (isBlank(passwordEnvironmentVariable)) {
      return null;
    }

    String passwordEnvironment = null;
    try {
      passwordEnvironment = System.getenv(passwordEnvironmentVariable);
    } catch (final Exception e) {
      throw new IllegalArgumentException(
          "Password could not be read from environmental variable <%s>"
              .formatted(passwordEnvironmentVariable),
          e);
    }

    return passwordEnvironment;
  }

  private String getPasswordFromFile() {
    if (passwordFile == null) {
      return null;
    }

    final Path normalizedPasswordFile = passwordFile.toAbsolutePath().normalize();
    if (!Files.isRegularFile(normalizedPasswordFile) || !Files.isReadable(normalizedPasswordFile)) {
      throw new IORuntimeException(
          "Password could not be read from file <%s> - path is not a readable regular file"
              .formatted(normalizedPasswordFile));
    }

    try {
      try (final BufferedReader reader = Files.newBufferedReader(normalizedPasswordFile, UTF_8)) {
        return reader.readLine();
      }
    } catch (final IOException e) {
      throw new IORuntimeException(
          "Password could not be read from file <%s>".formatted(normalizedPasswordFile), e);
    }
  }

  private String getPasswordPrompted() {
    return passwordPrompted;
  }

  private String getPasswordProvided() {
    return passwordProvided;
  }
}
