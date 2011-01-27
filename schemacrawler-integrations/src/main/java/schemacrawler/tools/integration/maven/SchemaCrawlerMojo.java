/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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


import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Locale;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.ConnectionOptions;
import schemacrawler.schemacrawler.DatabaseConnectionOptions;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.InfoLevel;
import schemacrawler.tools.options.OutputFormat;
import schemacrawler.tools.options.OutputOptions;
import sf.util.ObjectToString;
import sf.util.Utility;

/**
 * Generates a SchemaCrawler report of the database.
 * 
 * @goal schemacrawler
 * @execute phase="generate-sources"
 * @phase site
 */
public class SchemaCrawlerMojo
  extends AbstractMavenReport
  implements MavenReport
{

  /**
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  private MavenProject project;

  /**
   * JDBC driver classpath.
   * 
   * @parameter expression="${jdbc.driver.classpath}"
   *            alias="jdbc.driver.classpath"
   * @required
   */
  private String jdbcDriverClasspath;

  /**
   * Config file.
   * 
   * @parameter expression="${config}" alias="config"
   *            default-value="config.properties"
   * @required
   */
  private String config;

  /**
   * Config override file.
   * 
   * @parameter expression="${config-override}" alias="config-override"
   *            default-value="schemacrawler.config.override.properties"
   */
  private String configOverride;

  /**
   * JDBC driver class name.
   * 
   * @parameter expression="${driver}" alias="driver"
   * @required
   */
  private String driver;

  /**
   * Database connection string.
   * 
   * @parameter expression="${url}" alias="url"
   * @required
   */
  private String url;

  /**
   *Database connection user name.
   * 
   * @parameter expression="${user}" alias="user"
   * @required
   */
  private String user;

  /**
   *Database connection user password.
   * 
   * @parameter expression="${password}" alias="password"
   * @required
   */
  private String password;

  /**
   * Command.
   * 
   * @parameter expression="${command}" alias="command"
   * @required
   */
  private String command;

  /**
   * Sort tables alphabetically.
   * 
   * @parameter expression="${sorttables}" alias="sorttables"
   */
  private final String sorttables = "true";

  /**
   * Sort columns in a table alphabetically.
   * 
   * @parameter expression="${sortcolumns}" alias="sortcolumns"
   */
  private final String sortcolumns = "false";

  /**
   * Sort parameters in a stored procedure alphabetically.
   * 
   * @parameter expression="${sortinout}" alias="sortinout"
   */
  private final String sortinout = "false";

  /**
   * The info level determines the amount of database metadata
   * retrieved, and also determines the time taken to crawl the schema.
   * 
   * @parameter expression="${infolevel}" alias="infolevel"
   */
  private final String infolevel = InfoLevel.standard.name();

  /**
   * @parameter expression="${schemas}" alias="schemas"
   */
  private final String schemas = InclusionRule.ALL;

  /**
   * Comma-separated list of table types of
   * TABLE,VIEW,SYSTEM_TABLE,GLOBAL_TEMPORARY,LOCAL_TEMPORARY,ALIAS
   * 
   * @parameter expression="${table_types}" alias="table_types"
   */
  private String table_types;

  /**
   * Regular expression to match fully qualified table names, in the
   * form "CATALOGNAME.SCHEMANAME.TABLENAME" - for example,
   * .*\.C.*|.*\.P.* Tables that do not match the pattern are not
   * displayed.
   * 
   * @parameter expression="${tables}" alias="tables"
   */
  private final String tables = InclusionRule.ALL;

  /**
   * Regular expression to match fully qualified column names, in the
   * form "CATALOGNAME.SCHEMANAME.TABLENAME.COLUMNNAME" - for example,
   * .*\.STREET|.*\.PRICE matches columns named STREET or PRICE in any
   * table Columns that match the pattern are not displayed
   * 
   * @parameter expression="${excludecolumns}" alias="excludecolumns"
   */
  private final String excludecolumns = InclusionRule.NONE;

  /**
   * Regular expression to match fully qualified procedure names, in the
   * form "CATALOGNAME.SCHEMANAME.PROCEDURENAME" - for example,
   * .*\.C.*|.*\.P.* matches any procedures whose names start with C or
   * P Procedures that do not match the pattern are not displayed
   * 
   * @parameter expression="${schemas}" alias="schemas"
   */
  private final String procedures = InclusionRule.ALL;

  /**
   * Regular expression to match fully qualified parameter names.
   * Parameters that match the pattern are not displayed
   * 
   * @parameter expression="${schemas}" alias="schemas"
   */
  private final String excludeinout = InclusionRule.NONE;

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
   */
  public String getDescription(final Locale locale)
  {
    return "SchemaCrawler Report";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
   */
  public String getName(final Locale locale)
  {
    return "SchemaCrawler Report";
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#getOutputName()
   */
  public String getOutputName()
  {
    return "schemacrawler";
  }

  private OutputOptions createOutputOptions(final File outputFile)
  {
    final OutputOptions outputOptions = new OutputOptions();
    outputOptions.setOutputFormatValue(OutputFormat.html.name());
    outputOptions.setAppendOutput(false);
    outputOptions.setNoHeader(true);
    outputOptions.setNoFooter(true);
    outputOptions.setOutputFileName(outputFile.getAbsolutePath());
    return outputOptions;
  }

  private SchemaCrawlerOptions createSchemaCrawlerOptions()
  {
    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions();
    if (!Utility.isBlank(table_types))
    {
      schemaCrawlerOptions.setTableTypes(table_types);
    }
    schemaCrawlerOptions.setAlphabeticalSortForTables(Boolean
      .parseBoolean(sorttables));
    schemaCrawlerOptions.setAlphabeticalSortForTableColumns(Boolean
      .parseBoolean(sortcolumns));
    schemaCrawlerOptions.setAlphabeticalSortForProcedureColumns(Boolean
      .parseBoolean(sortinout));
    schemaCrawlerOptions.setSchemaInfoLevel(InfoLevel.valueOf(infolevel)
      .getSchemaInfoLevel());
    schemaCrawlerOptions
      .setSchemaInclusionRule(new InclusionRule(schemas, InclusionRule.NONE));
    schemaCrawlerOptions
      .setTableInclusionRule(new InclusionRule(tables, InclusionRule.NONE));
    schemaCrawlerOptions
      .setProcedureInclusionRule(new InclusionRule(procedures,
                                                   InclusionRule.NONE));
    schemaCrawlerOptions
      .setColumnInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                excludecolumns));
    schemaCrawlerOptions
      .setProcedureColumnInclusionRule(new InclusionRule(InclusionRule.ALL,
                                                         excludeinout));
    return schemaCrawlerOptions;
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
    URL[] jdbcJarUrls = new URL[0];
    try
    {

      final String[] jdbcJarPaths = jdbcDriverClasspath.split(System
        .getProperty("path.separator"));
      jdbcJarUrls = new URL[jdbcJarPaths.length];
      for (int i = 0; i < jdbcJarPaths.length; i++)
      {
        final String jdbcJarPath = jdbcJarPaths[i];
        jdbcJarUrls[i] = new File(jdbcJarPath).getCanonicalFile().toURI()
          .toURL();
      }

      final Method addUrlMethod = URLClassLoader.class
        .getDeclaredMethod("addURL", new Class[] {
          URL.class
        });

      final URLClassLoader classLoader = (URLClassLoader) getClass()
        .getClassLoader();

      addUrlMethod.setAccessible(true);

      for (final URL jdbcJarUrl: jdbcJarUrls)
      {
        addUrlMethod.invoke(classLoader, jdbcJarUrl);
      }

      getLog().info("Fixed SchemaCrawler classpath: "
                    + Arrays.asList(classLoader.getURLs()));

    }
    catch (final Exception e)
    {
      throw new MavenReportException("Error fixing classpath with "
                                     + Arrays.asList(jdbcJarUrls), e);
    }
  }

  @Override
  protected void executeReport(final Locale locale)
    throws MavenReportException
  {
    final Executable executable = new SchemaCrawlerExecutable(command);
    try
    {
      fixClassPath();

      final File outputFile = File.createTempFile("schemacrawler.report.",
                                                  ".html");
      final Config additionalConfiguration = Config
        .load(config, configOverride);

      // Execute SchemaCrawler
      executable.setOutputOptions(createOutputOptions(outputFile));
      executable.setSchemaCrawlerOptions(createSchemaCrawlerOptions());
      executable.setAdditionalConfiguration(additionalConfiguration);

      final ConnectionOptions connectionOptions = new DatabaseConnectionOptions(driver,
                                                                                url);
      connectionOptions.setUser(user);
      connectionOptions.setPassword(password);

      getLog().debug(ObjectToString.toString(executable));
      executable.execute(connectionOptions.createConnection());

      // Create report
      final String styleSheet = Utility
        .readResourceFully("/schemacrawler-report-output.css");

      final Sink sink = getSink();
      getLog().info(sink.getClass().getName());
      sink.head();
      sink.title();
      sink.text("SchemaCrawler Report");
      sink.title_();
      sink.rawText("<style type='text/css'>" + Utility.NEWLINE + styleSheet
                   + Utility.NEWLINE + "  </style>" + Utility.NEWLINE);
      sink.head_();

      sink.body();
      sink.rawText(Utility.readFully(new FileReader(outputFile)));
      sink.body_();

      sink.flush();
      sink.close();
    }
    catch (final Exception e)
    {
      throw new MavenReportException("Error executing:\n"
                                     + ObjectToString.toString(executable), e);
    }
  }

  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
   */
  @Override
  protected String getOutputDirectory()
  {
    return null; // Unused in the Maven API
  }

  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
   */
  @Override
  protected MavenProject getProject()
  {
    return project;
  }

  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
   */
  @Override
  protected Renderer getSiteRenderer()
  {
    return null; // Unused in the Maven API
  }

}
