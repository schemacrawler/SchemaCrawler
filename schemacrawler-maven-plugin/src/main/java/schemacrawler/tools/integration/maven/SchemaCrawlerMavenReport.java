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


import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.newBufferedReader;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static sf.util.Utility.NEWLINE;
import static sf.util.Utility.UTF8;
import static sf.util.Utility.flattenCommandlineArgs;
import static sf.util.Utility.readFully;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

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

import schemacrawler.Main;
import schemacrawler.tools.options.TextOutputFormat;

// Acknowledgements to Anthony Whitford for helping with getting this mojo in shape.
/**
 * Allows SchemaCrawler database schema reports in Maven generated
 * sites.
 */
@Mojo(name = "schemacrawler", defaultPhase = LifecyclePhase.SITE, requiresReports = true, threadSafe = true)
public class SchemaCrawlerMavenReport
  extends AbstractMavenReport
{

  private static Pattern bodyEnd = Pattern.compile(".*</body.*");
  private static Pattern bodyStart = Pattern.compile(".*<body.*");

  @Component
  private MavenProject project;

  @Parameter(property = "plugin.artifacts", readonly = true)
  private List<Artifact> pluginArtifacts;

  @Parameter(property = "additional-config", defaultValue = "schemacrawler.additional.config.properties")
  private String additionalconfigfile;
  @Parameter(property = "children")
  private int children;
  @Parameter(property = "command", required = true)
  private String command;
  @Parameter(property = "config", defaultValue = "schemacrawler.config.properties")
  private String configfile;
  @Parameter(property = "database", defaultValue = "${schemacrawler.database}")
  private String database;
  @Parameter(property = "driver", defaultValue = "${schemacrawler.driver}")
  private String driver;
  @Parameter(property = "excludecolumns")
  private String excludecolumns;
  @Parameter(property = "excludeinout")
  private String excludeinout;
  @Parameter(property = "grepcolumns", defaultValue = "false")
  private boolean grepcolumns;
  @Parameter(property = "grepdef", defaultValue = "false")
  private boolean grepdef;
  @Parameter(property = "grepinout", defaultValue = "false")
  private boolean grepinout;
  @Parameter(property = "host", defaultValue = "${schemacrawler.host}")
  private String host;
  @Parameter(property = "infolevel", required = true)
  private String infolevel;
  @Parameter(property = "invert-match", defaultValue = "false")
  private boolean invert_match;
  @Parameter(property = "loglevel")
  private String loglevel;
  @Parameter(property = "noinfo", defaultValue = "false")
  private boolean noinfo;
  @Parameter(property = "only-matching", defaultValue = "false")
  private boolean only_matching;
  @Parameter(property = "parents")
  private int parents;
  @Parameter(property = "password", defaultValue = "${schemacrawler.password}")
  private String password;
  @Parameter(property = "port", defaultValue = "${schemacrawler.port}")
  private String port;
  @Parameter(property = "portablenames", defaultValue = "false")
  private boolean portablenames;
  @Parameter(property = "routines")
  private String routines;
  @Parameter(property = "routinetypes")
  private String routinetypes;
  @Parameter(property = "schemas")
  private String schemas;
  @Parameter(property = "sequences")
  private String sequences;
  @Parameter(property = "server", defaultValue = "${schemacrawler.server}")
  private String server;
  @Parameter(property = "sortcolumns", defaultValue = "false")
  private boolean sortcolumns;
  @Parameter(property = "sortinout", defaultValue = "false")
  private boolean sortinout;
  @Parameter(property = "sorttables", defaultValue = "true")
  private boolean sorttables;
  @Parameter(property = "synonyms")
  private String synonyms;
  @Parameter(property = "tables")
  private String tables;
  @Parameter(property = "tabletypes")
  private String tabletypes;
  @Parameter(property = "url", defaultValue = "${schemacrawler.url}")
  private String url;
  @Parameter(property = "urlx", defaultValue = "${schemacrawler.urlx}")
  private String urlx;
  @Parameter(property = "user", defaultValue = "${schemacrawler.user}")
  private String user;

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

  private Path clipOutput(final Path completeOutputFile)
    throws IOException
  {
    final Path clippedOutputFile = createTempFile("schemacrawler", ".html");

    try (BufferedReader outputFileReader = newBufferedReader(completeOutputFile,
                                                             UTF8);
        Writer finalHtmlFileWriter = newBufferedWriter(clippedOutputFile,
                                                       UTF8,
                                                       CREATE,
                                                       TRUNCATE_EXISTING);)
    {
      finalHtmlFileWriter.append(NEWLINE);
      boolean skipLines = true;
      String line;
      while ((line = outputFileReader.readLine()) != null)
      {
        if (bodyEnd.matcher(line).matches())
        {
          break;
        }
        if (skipLines)
        {
          skipLines = !bodyStart.matcher(line).matches();
        }
        else
        {
          finalHtmlFileWriter.append(line).append(NEWLINE);
        }
      }
      finalHtmlFileWriter.append(NEWLINE);
    }

    return clippedOutputFile;
  }

  private Path executeSchemaCrawler()
    throws Exception
  {
    getLog();

    final Path outputFile = Files.createTempFile("schemacrawler.report.",
                                                 ".html");

    final String[] args = getCommandlineArgs(outputFile);
    Main.main(args);

    final Path clippedOutputFile = clipOutput(outputFile);
    return clippedOutputFile;
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

  private String[] getCommandlineArgs(final Path outputFile)
  {
    final Map<String, String> argsMap = new HashMap<>();
    argsMap.put("additionalconfigfile", additionalconfigfile);
    argsMap.put("children", String.valueOf(children));
    argsMap.put("command", command);
    argsMap.put("configfile", configfile);
    argsMap.put("database", database);
    argsMap.put("driver", driver);
    argsMap.put("excludecolumns", excludecolumns);
    argsMap.put("excludeinout", excludeinout);
    argsMap.put("grepcolumns", String.valueOf(grepcolumns));
    argsMap.put("grepdef", String.valueOf(grepdef));
    argsMap.put("grepinout", String.valueOf(grepinout));
    argsMap.put("host", host);
    argsMap.put("infolevel", infolevel);
    argsMap.put("invert-match", String.valueOf(invert_match));
    argsMap.put("loglevel", loglevel);
    argsMap.put("noinfo", String.valueOf(noinfo));
    argsMap.put("only-matching", String.valueOf(only_matching));
    argsMap.put("outputfile", outputFile.toAbsolutePath().toString());
    argsMap.put("outputformat", TextOutputFormat.html.getFormat());
    argsMap.put("parents", String.valueOf(parents));
    argsMap.put("password", password);
    argsMap.put("port", port);
    argsMap.put("portablenames", String.valueOf(portablenames));
    argsMap.put("routines", routines);
    argsMap.put("routinetypes", routinetypes);
    argsMap.put("schemas", schemas);
    argsMap.put("sequences", sequences);
    argsMap.put("server", server);
    argsMap.put("sortcolumns", String.valueOf(sortcolumns));
    argsMap.put("sortinout", String.valueOf(sortinout));
    argsMap.put("sorttables", String.valueOf(sorttables));
    argsMap.put("synonyms", synonyms);
    argsMap.put("tables", tables);
    argsMap.put("tabletypes", tabletypes);
    argsMap.put("url", url);
    argsMap.put("urlx", urlx);
    argsMap.put("user", user);

    return flattenCommandlineArgs(argsMap);
  }

}
