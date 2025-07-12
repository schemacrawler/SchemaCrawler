/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.integration.test.utility;

import static java.time.temporal.ChronoUnit.SECONDS;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

public class InformixContainer extends JdbcDatabaseContainer<InformixContainer> {

  private enum FileType {
    INIT_FILE,
    RUN_FILE_POST_INIT
  }

  private static final int INFORMIX_PORT = 9088;
  private static final String IFX_CONFIG_DIR = "/opt/ibm/config/";

  private String databaseName = "sysadmin";

  public InformixContainer(final DockerImageName dockerImageName) {
    super(dockerImageName);
  }

  @Override
  public String getDatabaseName() {
    return databaseName;
  }

  @Override
  public String getDriverClassName() {
    return "com.informix.jdbc.IfxDriver";
  }

  public Integer getJdbcPort() {
    return getMappedPort(INFORMIX_PORT);
  }

  @Override
  public String getJdbcUrl() {
    return String.format("jdbc:informix-sqli://%s:%d/%s", getHost(), getJdbcPort(), databaseName);
  }

  @Override
  public Set<Integer> getLivenessCheckPortNumbers() {
    return Collections.singleton(getJdbcPort());
  }

  @Override
  public String getPassword() {
    return "in4mix";
  }

  @Override
  public String getUsername() {
    return "informix";
  }

  @Override
  public InformixContainer withDatabaseName(final String databaseName) {
    this.databaseName = databaseName;
    return self();
  }

  public InformixContainer withInitFile(final MountableFile mountableFile) {
    setEnvAndCopyFile(mountableFile, FileType.INIT_FILE);
    return self();
  }

  @Override
  public InformixContainer withPassword(final String password) {
    throw new UnsupportedOperationException();
  }

  public InformixContainer withPostInitFile(final MountableFile mountableFile) {
    setEnvAndCopyFile(mountableFile, FileType.RUN_FILE_POST_INIT);
    return self();
  }

  @Override
  public InformixContainer withUrlParam(final String paramName, final String paramValue) {
    throw new UnsupportedOperationException();
  }

  @Override
  public InformixContainer withUsername(final String username) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void configure() {
    super.configure();
    addExposedPort(INFORMIX_PORT);
    addEnv("LICENSE", "accept");
    withPrivilegedMode(true)
        .waitingFor(
            new LogMessageWaitStrategy()
                .withRegEx(".*Maximum server connections 1.*")
                .withTimes(1)
                .withStartupTimeout(Duration.of(60, SECONDS)));
  }

  @Override
  protected String getTestQueryString() {
    return "select today from systables where tabid = 1";
  }

  @Override
  protected void waitUntilContainerStarted() {
    getWaitStrategy().waitUntilReady(this);
  }

  private void setEnvAndCopyFile(final MountableFile mountableFile, final FileType fileType) {
    addEnv(
        fileType.toString(), Paths.get(mountableFile.getFilesystemPath()).getFileName().toString());
    withCopyFileToContainer(mountableFile, IFX_CONFIG_DIR);
  }
}
