/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.testdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
    description = "Creates a test database schema for testing SchemaCrawler",
    name = "Test Schema Creator",
    mixinStandardHelpOptions = true)
public class TestSchemaCreatorMain implements Callable<Integer> {

  public static int call(final String... args) {
    final int exitCode = new CommandLine(new TestSchemaCreatorMain()).execute(args);
    return exitCode;
  }

  public static void main(final String... args) {
    System.out.printf("args=%s%n", Arrays.asList(args));
    final int exitCode = call(args);
    if (exitCode != 0) {
      throw new RuntimeException(
          "%s has exited with error %d"
              .formatted(TestSchemaCreatorMain.class.getSimpleName(), exitCode));
    }
  }

  @CommandLine.Option(
      names = {"--url"},
      required = true,
      description = "JDBC connection URL to the database",
      paramLabel = "<url>")
  private String connectionUrl;

  @CommandLine.Option(
      names = {"--user"},
      description = "Database user name",
      paramLabel = "<user>")
  private String user;

  @CommandLine.Option(
      names = {"--password"},
      description = "Database password",
      paramLabel = "<password>")
  private String passwordProvided;

  @CommandLine.Option(
      names = {"--scripts-resource"},
      description = "Scripts resource on CLASSPATH",
      paramLabel = "<scripts-resource>")
  private String scriptsresource;

  @CommandLine.Option(
      names = {"--debug", "-d"},
      description = "Debug trace")
  private boolean debug;

  private TestSchemaCreatorMain() {}

  @Override
  public Integer call() {
    try (final Connection connection =
        DriverManager.getConnection(connectionUrl, user, passwordProvided)) {
      findScriptsResource();
      final TestSchemaCreator testSchemaCreator =
          new TestSchemaCreator(connection, scriptsresource, debug);
      testSchemaCreator.run();
    } catch (final Exception e) {
      e.printStackTrace();
      return 1;
    }
    return 0;
  }

  private void findScriptsResource() {
    if (scriptsresource != null && !scriptsresource.isEmpty()) {
      return;
    }
    if (connectionUrl == null) {
      throw new IllegalArgumentException("No connection URL provided");
    }
    final String[] splitUrl = connectionUrl.split(":");
    if (splitUrl.length < 2) {
      throw new IllegalArgumentException("No connection URL provided");
    }
    scriptsresource = "/%s.scripts.txt".formatted(splitUrl[1]);
  }
}
