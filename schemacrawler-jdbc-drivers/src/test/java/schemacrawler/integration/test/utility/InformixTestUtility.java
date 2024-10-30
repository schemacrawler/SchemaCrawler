package schemacrawler.integration.test.utility;

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
