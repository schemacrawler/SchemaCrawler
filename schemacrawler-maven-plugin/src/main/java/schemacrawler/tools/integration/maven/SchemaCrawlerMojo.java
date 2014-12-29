/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import static java.nio.file.Files.newBufferedReader;
import static sf.util.Utility.UTF8;
import static sf.util.Utility.isBlank;
import static sf.util.Utility.readFully;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.RegularExpressionExclusionRule;
import schemacrawler.schemacrawler.RegularExpressionInclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.TextOutputFormat;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import sf.util.ObjectToString;
import sf.util.Utility;

/**
 * Acknowledgements to Anthony Whitford for helping with getting this
 * mojo in shape.
 */
@Mojo(name = "schemacrawler", defaultPhase = LifecyclePhase.SITE, requiresReports = true, threadSafe = true)
public class SchemaCrawlerMojo
  extends AbstractMavenReport
{

  @Component
  private MavenProject project;

  /**
   * Config file.
   */
  @Parameter(property = "config", defaultValue = "schemacrawler.config.properties")
  private String config;

  /**
   * Additional config file.
   */
  @Parameter(property = "additional-config", defaultValue = "schemacrawler.additional.config.properties")
  private String additionalConfig;

  /**
   * Database server type.
   */
  @Parameter(property = "server", defaultValue = "${schemacrawler.server}")
  private String server;

  /**
   * JDBC driver class name.
   */
  @Parameter(property = "driver", defaultValue = "${schemacrawler.driver}")
  private String driver;

  /**
   * Database connection string.
   */
  @Parameter(property = "url", defaultValue = "${schemacrawler.url}")
  private String url;

  /**
   * Database connection user name.
   */
  @Parameter(property = "user", defaultValue = "${schemacrawler.user}")
  private String user;

  /**
   * Database connection user password.
   */
  @Parameter(property = "password", defaultValue = "${schemacrawler.password}")
  private String password;

  /**
   * SchemaCrawler command.
   */
  @Parameter(property = "command")
  private String command;

  /**
   * The plugin dependencies.
   */
  @Parameter(property = "plugin.artifacts", readonly = true)
  private List<Artifact> pluginArtifacts;

  /**
   * Sort tables alphabetically.
   */
  @Parameter(property = "sorttables", defaultValue = "true")
  private boolean sorttables;

  /**
   * Sort columns in a table alphabetically.
   */
  @Parameter(property = "sortcolumns", defaultValue = "false")
  private boolean sortcolumns;

  /**
   * Sort parameters in a routine alphabetically.
   */
  @Parameter(property = "sortinout", defaultValue = "false")
  private boolean sortinout;

  /**
   * The info level determines the amount of database metadata
   * retrieved, and also determines the time taken to crawl the schema.
   */
  @Parameter(property = "infolevel", defaultValue = "standard")
  private String infolevel;

  /**
   * Schemas to include.
   */
  @Parameter(property = "schemas", defaultValue = ALL)
  private String schemas;

  /**
   * Comma-separated list of table types of
   * TABLE,VIEW,SYSTEM_TABLE,GLOBAL_TEMPORARY,LOCAL_TEMPORARY,ALIAS
   */
  @Parameter(property = "table_types")
  private String table_types;

  /**
   * Regular expression to match fully qualified table names, in the
   * form "CATALOGNAME.SCHEMANAME.TABLENAME" - for example,
   * .*\.C.*|.*\.P.* Tables that do not match the pattern are not
   * displayed.
   */
  @Parameter(property = "tables", defaultValue = ALL)
  private String tables;

  private static final String ALL = ".*";
  private static final String NONE = "";

  /**
   * Regular expression to match fully qualified column names, in the
   * form "CATALOGNAME.SCHEMANAME.TABLENAME.COLUMNNAME" - for example,
   * .*\.STREET|.*\.PRICE matches columns named STREET or PRICE in any
   * table Columns that match the pattern are not displayed
   */
  @Parameter(property = "excludecolumns", defaultValue = NONE)
  private String excludecolumns;

  /**
   * Regular expression to match fully qualified routine names, in the
   * form "CATALOGNAME.SCHEMANAME.ROUTINENAME" - for example,
   * .*\.C.*|.*\.P.* matches any routines whose names start with C or P
   * Routines that do not match the pattern are not displayed
   */
  @Parameter(property = "routines", defaultValue = ALL)
  private String routines;

  /**
   * Regular expression to match fully qualified parameter names.
   * Parameters that match the pattern are not displayed
   */
  @Parameter(property = "excludeinout", defaultValue = NONE)
  private String excludeinout;

  @Override
  public boolean canGenerateReport()
  {
    // TODO Check if the connection to the database is good
    return true;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
   */
  @Override
  public String getDescription(final Locale locale)
  {
    return "SchemaCrawler Report";
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
   */
  @Override
  public String getName(final Locale locale)
  {
    return "SchemaCrawler Report";
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getOutputName()
   */
  @Override
  public String getOutputName()
  {
    return "schemacrawler";
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
   */
  @Override
  protected void executeReport(final Locale locale)
    throws MavenReportException
  {
    final Log logger = getLog();

    try
    {
      fixClassPath();

      final Path outputFile = executeSchemaCrawler();

      final Sink sink = getSink();
      logger.info(sink.getClass().getName());

      sink
        .rawText("<link rel=\"stylesheet\" href=\"./css/schemacrawler-output.css\" type=\"text/css\"/>\n");
      sink.rawText(readFully(newBufferedReader(outputFile, UTF8)));

      sink.flush();
    }
    catch (final Exception e)
    {
      throw new MavenReportException("Error executing SchemaCrawler command "
                                     + command, e);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
   */
  @Override
  protected String getOutputDirectory()
  {
    return null; // Unused in the Maven API
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
   */
  @Override
  protected MavenProject getProject()
  {
    return project;
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
   */
  @Override
  protected Renderer getSiteRenderer()
  {
    return null; // Unused in the Maven API
  }

  /**
   * Load configuration files, and add in other configuration options.
   *
   * @return SchemaCrawler command configuration
   */
  private Config createAdditionalConfiguration()
  {
    final SchemaTextOptions textOptions = new SchemaTextOptions();
    textOptions.setNoHeader(true);
    textOptions.setNoFooter(true);
    textOptions.setAlphabeticalSortForTables(sorttables);
    textOptions.setAlphabeticalSortForTableColumns(sortcolumns);
    textOptions.setAlphabeticalSortForRoutineColumns(sortinout);

    final Config additionalConfiguration = new Config();
    try
    {
      additionalConfiguration.putAll(Config.load(config, additionalConfig));
    }
    catch (IOException e)
    {
      getLog().info(String.format("Cannot read configuration files, %s and %s",
                                  config,
                                  additionalConfiguration),
                    e);
    }
    additionalConfiguration.putAll(textOptions.toConfig());
    return additionalConfiguration;
  }

  private ConnectionOptions createConnectionOptions()
    throws SchemaCrawlerException
  {
    final ConnectionOptions connectionOptions = new DatabaseConnectionOptions(driver,
                                                                              url);
    connectionOptions.setUser(user);
    connectionOptions.setPassword(password);
    return connectionOptions;
  }

  /**
   * Defensively set SchemaCrawlerOptions.
   *
   * @return SchemaCrawlerOptions
   */
  private SchemaCrawlerOptions createSchemaCrawlerOptions()
  {
    final Log logger = getLog();

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();

    if (!isBlank(table_types))
    {
      schemaCrawlerOptions.setTableTypesFromString(table_types);
    }

    if (!isBlank(infolevel))
    {
      final SchemaInfoLevel schemaInfoLevel = InfoLevel
        .valueOfFromString(infolevel).getSchemaInfoLevel();
      schemaCrawlerOptions.setSchemaInfoLevel(schemaInfoLevel);
    }

    schemaCrawlerOptions
      .setSchemaInclusionRule(new RegularExpressionInclusionRule(StringUtils
        .defaultString(schemas, ALL)));
    schemaCrawlerOptions
      .setTableInclusionRule(new RegularExpressionInclusionRule(StringUtils
        .defaultString(tables, ALL)));
    schemaCrawlerOptions
      .setRoutineInclusionRule(new RegularExpressionInclusionRule(StringUtils
        .defaultString(routines, ALL)));

    schemaCrawlerOptions
      .setColumnInclusionRule(new RegularExpressionExclusionRule(StringUtils
        .defaultString(excludecolumns, NONE)));
    schemaCrawlerOptions
      .setColumnInclusionRule(new RegularExpressionExclusionRule(StringUtils
        .defaultString(excludeinout, NONE)));

    logger.info(ObjectToString.toString(schemaCrawlerOptions));
    return schemaCrawlerOptions;
  }

  private Path executeSchemaCrawler()
    throws Exception
  {
    final Log logger = getLog();

    final Path outputFile = Files.createTempFile("schemacrawler.report.",
                                                 ".html");

    final SchemaCrawlerOptions schemaCrawlerOptions = createSchemaCrawlerOptions();
    final ConnectionOptions connectionOptions = createConnectionOptions();
    final Config additionalConfiguration = createAdditionalConfiguration();
    final OutputOptions outputOptions = new OutputOptions(TextOutputFormat.html,
                                                          outputFile);

    final Executable executable = new SchemaCrawlerExecutable(command);
    executable.setOutputOptions(outputOptions);
    executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
    executable.setAdditionalConfiguration(additionalConfiguration);

    logger.debug(ObjectToString.toString(executable));
    executable.execute(connectionOptions.getConnection());

    return outputFile;
  }

  /**
   * The JDBC driver classpath comes from the configuration of the
   * SchemaCrawler plugin. The current classloader needs to be "fixed"
   * to include the JDBC driver in the classpath.
   *
   * @throws MavenReportException
   */
  private void fixClassPath()
    throws MavenReportException
  {

    final Log logger = getLog();

    try
    {
      final List<URL> jdbcJarUrls = new ArrayList<URL>();
      for (final Object artifact: project.getArtifacts())
      {
        jdbcJarUrls.add(((Artifact) artifact).getFile().toURI().toURL());
      }
      for (final Artifact artifact: pluginArtifacts)
      {
        jdbcJarUrls.add(artifact.getFile().toURI().toURL());
      }
      logger.debug("SchemaCrawler - Maven Plugin: classpath: " + jdbcJarUrls);

      final Method addUrlMethod = URLClassLoader.class
        .getDeclaredMethod("addURL", new Class[] {
          URL.class
        });
      addUrlMethod.setAccessible(true);

      final URLClassLoader classLoader = (URLClassLoader) getClass()
        .getClassLoader();

      for (final URL jdbcJarUrl: jdbcJarUrls)
      {
        addUrlMethod.invoke(classLoader, jdbcJarUrl);
      }

      logger.info("Fixed SchemaCrawler classpath: "
                  + Arrays.asList(classLoader.getURLs()));

    }
    catch (final Exception e)
    {
      throw new MavenReportException("Error fixing classpath", e);
    }
  }

}
