/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test.utility;

import com.github.dockerjava.api.command.InspectContainerResponse;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.cassandra.CassandraDatabaseDelegate;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public final class CassandraTestUtility {

  public static CassandraContainer newCassandraContainer() {
    return newCassandraContainer("5.0.4");
  }

  @SuppressWarnings("resource")
  private static CassandraContainer newCassandraContainer(final String version) {

    class CassandraInitContainer extends CassandraContainer {

      private String containerInitScriptPath;

      public CassandraInitContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
      }

      public CassandraInitContainer withContainerInitScript(final String containerInitScriptPath) {
        this.containerInitScriptPath = containerInitScriptPath;
        return this;
      }

      @Override
      protected void containerIsStarted(final InspectContainerResponse containerInfo) {
        if (containerInitScriptPath == null) {
          return;
        }
        // Apply init script to the database
        try {
          new CassandraDatabaseDelegate(this)
              .execute(null, containerInitScriptPath, -1, false, false);
        } catch (ScriptUtils.ScriptStatementFailedException e) {
          logger().error("Error while executing init script: {}", containerInitScriptPath, e);
          throw new ScriptUtils.UncategorizedScriptException(
              "Error while executing init script: " + containerInitScriptPath, e);
        }
      }
    }

    final DockerImageName imageName = DockerImageName.parse("cassandra");

    final CassandraContainer dbContainer =
        new CassandraInitContainer(imageName.withTag(version))
            .withContainerInitScript("cassandra.create-database.cql")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("cassandra.create-database.cql"),
                "cassandra.create-database.cql")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("cassandra.yaml"),
                "/etc/cassandra/cassandra.yaml")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("cassandra.keystore"),
                "/security/cassandra.keystore")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("cassandra.truststore"),
                "/security/cassandra.truststore");

    return dbContainer;
  }

  private CassandraTestUtility() {
    // Prevent instantiation
  }
}
