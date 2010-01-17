/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.integration.maven;


import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;

/**
 * Generates a SchemaCrawler report of the database.
 *
 * @goal schemacrawler
 * @execute phase="generate-sources"
 */
public class SchemaCrawlerMojo
  extends AbstractMavenReport
  implements MavenReport {

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project;

  /**
   * @component
   */
  private Renderer siteRenderer;

  /**
   * JDBC driver classpath.
   *
   * @parameter expression="${schemacrawler.jdbc.driver.classpath}" alias="schemacrawler.jdbc.driver.classpath"
   * @required
   */
  private String jdbcDriverClasspath;

  /**
   * Config file.
   *
   * @parameter expression="${schemacrawler.config}" alias="schemacrawler.config" default-value="schemacrawler.config.properties"
   * @required
   */
  private String config;

  /**
   * Config override file.
   *
   * @parameter expression="${schemacrawler.config-override}" alias="schemacrawler.config-override"
   * default-value="schemacrawler.config.override.properties"
   */
  private String configOverride;

  /**
   * Datasource.
   *
   * @parameter expression="${schemacrawler.datasource}" alias="schemacrawler.datasource"
   * @required
   */
  private String datasource;

  /**
   * Command.
   *
   * @parameter expression="${schemacrawler.command}" alias="schemacrawler.command"
   * @required
   */
  private String command;

  /**
   * Whether the header should be suppressed.
   *
   * @parameter expression="${schemacrawler.no-header}" alias="schemacrawler.no-header" default-value="false"
   */
  private boolean noHeader;

  /**
   * Whether the footer should be suppressed.
   *
   * @parameter expression="${schemacrawler.no-footer}" alias="schemacrawler.no-footer" default-value="false"
   */
  private boolean noFooter;

  /**
   * Whether the info should be suppressed.
   *
   * @parameter expression="${schemacrawler.no-info}" alias="schemacrawler.no-footer" default-value="false"
   */
  private boolean noInfo;

  /**
   * Output format.
   *
   * @parameter expression="${schemacrawler.outputformat}" alias="schemacrawler.outputformat" default-value="text"
   */
  private String outputFormat;

  /**
   * Output file.
   *
   * @parameter expression="${schemacrawler.outputfile}" alias="schemacrawler.outputfile"
   * default-value="schemacrawler.report.html"
   */
  private String outputFile;

  /**
   * Whether to append to the output.
   *
   * @parameter expression="${schemacrawler.append}" alias="schemacrawler.append" default-value="false"
   */
  private boolean append;

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#canGenerateReport()
   */
  @Override
  public boolean canGenerateReport() {
    // TODO: Test database connection?
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute()
    throws MojoExecutionException {
    final String errorMessage = "An error has occurred in "
      + getName(Locale.ENGLISH)
      + " report generation.";
    try {
      final String outputDirectory = getOutputDirectory();
      final SiteRendererSink sink = siteRenderer
        .createSink(new File(outputDirectory), getOutputName() + ".html");
      generate(sink, Locale.getDefault());
    }
    catch (final RendererException e) {
      throw new MojoExecutionException(errorMessage, e);
    }
    catch (final IOException e) {
      throw new MojoExecutionException(errorMessage, e);
    }
    catch (final MavenReportException e) {
      throw new MojoExecutionException(errorMessage, e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#generate(org.codehaus.doxia.sink.Sink, java.util.Locale)
   */
  @Override
  public void generate(final Sink sink, final Locale locale)
    throws MavenReportException {
    executeReport(locale);
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getCategoryName()
   */
  @Override
  public String getCategoryName() {
    return CATEGORY_PROJECT_REPORTS;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
   */
  public String getDescription(final Locale locale) {
    return "SchemaCrawler Report";
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
   */
  public String getName(final Locale locale) {
    return "SchemaCrawler Report";
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getOutputName()
   */
  public String getOutputName() {
    final String outputFilename = new File(outputFile).getName();
    return outputFilename.substring(0, outputFilename.lastIndexOf("."));
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getReportOutputDirectory()
   */
  @Override
  public File getReportOutputDirectory() {
    return new File(outputFile).getParentFile();
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#isExternalReport()
   */
  @Override
  public boolean isExternalReport() {
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#setReportOutputDirectory(java.io.File)
   */
  @Override
  public void setReportOutputDirectory(final File directory) {
    // Get the output filename
    final String outputFilename = new File(outputFile).getName();
    // Set the new path
    if (directory.exists() && directory.isDirectory()) {
      outputFile = new File(directory, outputFilename).getAbsolutePath();
    }
  }

  @Override
  protected void executeReport(final Locale locale)
    throws MavenReportException {

    // Build command line
    final String[] args = new String[]{
      "-g",
      config,
      "-p",
      configOverride,
      "-c",
      datasource,
      "-command",
      command,
      "-noheader=" + noHeader,
      "-nofooter=" + noFooter,
      "-noinfo=" + noInfo,
      "-outputformat",
      outputFormat,
      "-outputfile",
      outputFile,
      "-append",
      String.valueOf(append),
    };

    // Execute command
    final String commandLine = schemacrawler.Main.class.getName() + " ~"
      + Arrays.asList(args);
    try {
      fixClassPath();
      getLog().info(commandLine);
      schemacrawler.Main
        .main(args);
    }
    catch (final Exception e) {
      throw new MavenReportException("Error executing: " + commandLine, e);
    }
  }

  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
   */
  @Override
  protected String getOutputDirectory() {
    return new File(outputFile).getParentFile()
      .getAbsolutePath();
  }

  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
   */
  @Override
  protected MavenProject getProject() {
    return project;
  }

  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
   */
  @Override
  protected Renderer getSiteRenderer() {
    return siteRenderer;
  }

  /**
   * The JDBC driver classpath comes from the configuration of the SchemaCrawler plugin. The current classloader needs
   * to be "fixed" to include the JDBC driver in the classpath.
   *
   * @throws MavenReportException
   */
  private void fixClassPath()
    throws MavenReportException {
    URL[] jdbcJarUrls = new URL[0];
    try {

      final String[] jdbcJarPaths = jdbcDriverClasspath.split(System
        .getProperty("path.separator"));
      jdbcJarUrls = new URL[jdbcJarPaths.length];
      for (int i = 0; i < jdbcJarPaths.length; i++) {
        final String jdbcJarPath = jdbcJarPaths[i];
        jdbcJarUrls[i] = new File(jdbcJarPath).getCanonicalFile()
          .toURI()
          .toURL();
      }

      final Method addUrlMethod = new URLClassLoader(new URL[0]).getClass()
        .getDeclaredMethod("addURL", new Class[]{
          URL.class
        });

      final URLClassLoader classLoader = (URLClassLoader) this.getClass()
        .getClassLoader();

      addUrlMethod.setAccessible(true);

      for (final URL jdbcJarUrl : jdbcJarUrls) {
        addUrlMethod.invoke(classLoader, jdbcJarUrl);
      }

      getLog().info("Fixed SchemaCrawler classpath: "
        + Arrays.asList(classLoader.getURLs()));

    }
    catch (final Exception e) {
      throw new MavenReportException("Error fixing classpath with "
        + Arrays.asList(jdbcJarUrls), e);
    }
  }

}
