/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test.utility;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;

public final class OracleTestUtility {

  @SuppressWarnings("resource")
  public static JdbcDatabaseContainer<?> newOracleContainer() {
    return newOracleContainer("23.9-slim-faststart");
  }

  @SuppressWarnings("resource")
  private static OracleContainer newOracleContainer(final String version) {
    class OracleFreeContainer extends OracleContainer {
      OracleFreeContainer(final DockerImageName dockerImageName) {
        super(dockerImageName);
        List<Integer> ports = new ArrayList<>();
        ports.add(1521);
        setExposedPorts(ports);
        waitStrategy =
            new LogMessageWaitStrategy()
                .withRegEx(".*DATABASE IS READY TO USE!.*\\s")
                .withTimes(1)
                .withStartupTimeout(Duration.ofMinutes(5));
      }

      @Override
      public String getDatabaseName() {
        return "freepdb1";
      }
    }

    final DockerImageName imageName =
        DockerImageName.parse("gvenzl/oracle-free").asCompatibleSubstituteFor("gvenzl/oracle-xe");
    return new OracleFreeContainer(imageName.withTag(version))
        .withStartupTimeout(Duration.ofMinutes(5));
  }

  private OracleTestUtility() {
    // Prevent instantiation
  }
}
