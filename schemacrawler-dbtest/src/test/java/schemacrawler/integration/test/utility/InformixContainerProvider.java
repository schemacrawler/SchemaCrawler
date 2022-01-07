package schemacrawler.integration.test.utility;

import org.testcontainers.containers.JdbcDatabaseContainerProvider;
import org.testcontainers.utility.DockerImageName;

public class InformixContainerProvider extends JdbcDatabaseContainerProvider {

  private static final DockerImageName imageName =
      DockerImageName.parse("ibmcom/informix-developer-database");

  @Override
  public InformixContainer newInstance(final String tag) {
    return new InformixContainer(imageName.withTag(tag));
    // .withInitFile(MountableFile.forClasspathResource("/db/books/01_schemas_01_F.sql"));
  }

  @Override
  public boolean supports(final String databaseType) {
    return databaseType.equals("informix");
  }
}
