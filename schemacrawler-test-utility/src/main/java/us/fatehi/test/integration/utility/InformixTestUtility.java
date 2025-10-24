/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.integration.utility;

import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class InformixTestUtility {

  @SuppressWarnings("resource")
  public static InformixContainer newInformixContainer() {
    return new InformixContainer(
            DockerImageName.parse("ibmcom/informix-developer-database").withTag("14.10.FC7W1DE"))
        .withDatabaseName("books")
        .withInitFile(MountableFile.forClasspathResource("create-books-database.sql"));
  }
}
