/*
 * SchemaCrawler AI
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;

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
  @DisplayName("Docker image starts successfully and SchemaCrawler script runs")
  public void testDockerImageHealth() throws IOException, InterruptedException {

    LOGGER.log(Level.CONFIG, "Verifying " + DOCKER_IMAGE_NAME);

    // Run SchemaCrawler command and capture output
    final ExecResult result =
        mcpServerContainer.execInContainer("/opt/schemacrawler/bin/schemacrawler.sh", "-V");

    // Assert successful execution
    assertCommandExecution(result);
  }

  @Test
  @DisplayName("Docker image exposes `schemacrawler` launcher")
  public void testDockerImageSchemaCrawlerLauncher() throws IOException, InterruptedException {

    LOGGER.log(Level.CONFIG, "Verifying `schemacrawler` launcher in " + DOCKER_IMAGE_NAME);

    // Run SchemaCrawler alias and capture output
    final ExecResult result = mcpServerContainer.execInContainer("schemacrawler", "-V");

    // Assert successful execution
    assertCommandExecution(result);
  }

  @Test
  @DisplayName("Docker image exposes `schemaspy` launcher")
  public void testDockerImageSchemaSpyLauncher() throws IOException, InterruptedException {

    LOGGER.log(Level.CONFIG, "Verifying `schemaspy` launcher in " + DOCKER_IMAGE_NAME);

    // Run SchemaSpy alias and capture output
    final ExecResult result = mcpServerContainer.execInContainer("schemaspy", "-V");

    // Assert successful execution
    assertCommandExecution(result);
    assertThat(
        result.getStdout().contains("SchemaSpy 7.x adapter for generating OKF bundles."), is(true));
  }

  private void assertCommandExecution(final ExecResult result) {
    assertThat(result.getExitCode(), is(0));
    assertThat(result.getStdout(), containsString(Version.version().toString()));
    assertThat(result.getStderr(), is(emptyString()));
  }
}
