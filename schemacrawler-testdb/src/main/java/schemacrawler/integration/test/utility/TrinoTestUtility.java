/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.integration.test.utility;

import org.testcontainers.containers.TrinoContainer;
import org.testcontainers.utility.DockerImageName;

public final class TrinoTestUtility {

  public static TrinoContainer newTrinoContainer() {
    return newTrinoContainer("470");
  }

  private static TrinoContainer newTrinoContainer(final String version) {

    final DockerImageName imageName = DockerImageName.parse("trinodb/trino");

    final TrinoContainer dbContainer = new TrinoContainer(imageName.withTag(version));

    return dbContainer;
  }

  private TrinoTestUtility() {
    // Prevent instantiation
  }
}
