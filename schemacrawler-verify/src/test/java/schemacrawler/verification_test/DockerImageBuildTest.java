/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: CC-BY-NC-4.0
 */

package schemacrawler.verification_test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.startsWith;

import java.io.IOException;
import java.time.Duration;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container.ExecResult;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import schemacrawler.schemacrawler.Version;
import us.fatehi.utility.readconfig.SystemPropertiesConfig;

/** Integration test to verify the published Docker image is viable by starting it. */
@Testcontainers
@Tag("docker")
@DisplayName("Test Docker image build")
public class DockerImageBuildTest {

  private static final Logger LOGGER =
      Logger.getLogger(DockerImageBuildTest.class.getCanonicalName());

  private static final DockerImageName DOCKER_IMAGE_NAME =
      DockerImageName.parse("schemacrawler/schemacrawler")
          .withTag(new SystemPropertiesConfig().getStringValue("docker_image_tag", "latest"));

  @Container
  private final GenericContainer<?> mcpServerContainer =
      new GenericContainer<>(DOCKER_IMAGE_NAME)
          .withImagePullPolicy(imageName -> false)
          .withStartupTimeout(Duration.ofSeconds(60))
          .withCommand("tail", "-f", "/dev/null");

  @Test
  @DisplayName("Docker image starts successfully and SchemaCrawler runs")
  public void testDockerImageHealth() throws IOException, InterruptedException {

    LOGGER.log(Level.CONFIG, "Verifying " + DOCKER_IMAGE_NAME);

    // Run SchemaCrawler command and capture output
    final ExecResult result =
        mcpServerContainer.execInContainer("/opt/schemacrawler/bin/schemacrawler.sh", "-V");

    // Assert successful execution
    assertThat(result.getExitCode(), is(0));
    assertThat(result.getStdout(), startsWith(Version.version().toString()));
    assertThat(result.getStderr(), is(emptyString()));
  }
}
