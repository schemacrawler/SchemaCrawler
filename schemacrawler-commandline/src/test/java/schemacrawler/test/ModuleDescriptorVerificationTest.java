/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

/**
 * Integration test to verify the schemacrawler module descriptor by inspecting the actual jar
 * file. This test uses the jar command to examine the module-info.class.
 */
public class ModuleDescriptorVerificationTest {

  /**
   * Verifies that the schemacrawler jar contains a module-info and that it does not export the
   * crawl package.
   */
  @Test
  public void testJarContainsModuleInfoWithoutCrawlExport() throws Exception {
    // Get version dynamically from the Version class
    final String version = schemacrawler.Version.version().getProductVersion();
    
    // Find the schemacrawler jar in the local Maven repository
    final String userHome = System.getProperty("user.home");
    final Path schemacrawlerJarPath =
        Paths.get(
            userHome,
            ".m2",
            "repository",
            "us",
            "fatehi",
            "schemacrawler",
            version,
            "schemacrawler-" + version + ".jar");

    final File jarFile = schemacrawlerJarPath.toFile();
    if (!jarFile.exists()) {
      // Jar not yet built or not in repository - skip test
      System.out.println(
          "Skipping test: schemacrawler jar not found at " + schemacrawlerJarPath);
      return;
    }

    // Use java command to examine module descriptor
    final ProcessBuilder pb =
        new ProcessBuilder(
            "java",
            "--module-path",
            jarFile.getAbsolutePath(),
            "--describe-module",
            "us.fatehi.schemacrawler");
    pb.redirectErrorStream(true);
    final Process process = pb.start();

    final StringBuilder output = new StringBuilder();
    try (final BufferedReader reader =
        new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
    }

    final int exitCode = process.waitFor();
    assertThat("java command should succeed", exitCode, is(0));

    final String moduleInfo = output.toString();
    assertThat("Module info should not be empty", moduleInfo.length() > 0, is(true));

    // Verify that crawl package is NOT exported
    assertThat(
        "Module should not export schemacrawler.crawl",
        moduleInfo,
        not(containsString("exports schemacrawler.crawl")));

    // Verify that it CONTAINS the crawl package (but doesn't export it)
    assertThat(
        "Module should contain schemacrawler.crawl",
        moduleInfo,
        containsString("contains schemacrawler.crawl"));

    // Verify that public packages ARE exported
    assertThat(
        "Module should export schemacrawler.schema",
        moduleInfo,
        containsString("exports schemacrawler.schema"));
    assertThat(
        "Module should export schemacrawler.schemacrawler",
        moduleInfo,
        containsString("exports schemacrawler.schemacrawler"));
  }
}
