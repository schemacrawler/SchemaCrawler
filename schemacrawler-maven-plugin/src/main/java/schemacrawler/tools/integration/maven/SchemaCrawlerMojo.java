package schemacrawler.tools.integration.maven;


import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.doxia.siterenderer.RendererException;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;

/**
 * Generates a SchemaCrawler report of the database.
 * 
 * @goal schemacrawler
 * @execute phase="generate-sources"
 */
public class SchemaCrawlerMojo
  extends AbstractMavenReport
  implements MavenReport
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerMojo.class
    .getName());

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
   * Config file.
   * 
   * @parameter expression="${schemacrawler.config}"
   *            alias="schemacrawler.config"
   *            default-value="schemacrawler.config.properties"
   * @required
   */
  private String config;

  /**
   * Config override file.
   * 
   * @parameter expression="${schemacrawler.config-override}"
   *            alias="schemacrawler.config-override"
   *            default-value="schemacrawler.config.override.properties"
   */
  private String configOverride;

  /**
   * Datasource.
   * 
   * @parameter expression="${schemacrawler.datasource}"
   *            alias="schemacrawler.datasource"
   * @required
   */
  private String datasource;

  /**
   * Command.
   * 
   * @parameter expression="${schemacrawler.command}"
   *            alias="schemacrawler.command"
   * @required
   */
  private String command;

  /**
   * Whether the header should be suppressed.
   * 
   * @parameter expression="${schemacrawler.no-header}"
   *            alias="schemacrawler.no-header" default-value="false"
   */
  private boolean noHeader;

  /**
   * Whether the footer should be suppressed.
   * 
   * @parameter expression="${schemacrawler.no-footer}"
   *            alias="schemacrawler.no-footer" default-value="false"
   */
  private boolean noFooter;

  /**
   * Whether the info should be suppressed.
   * 
   * @parameter expression="${schemacrawler.no-info}"
   *            alias="schemacrawler.no-footer" default-value="false"
   */
  private boolean noInfo;

  /**
   * Output format.
   * 
   * @parameter expression="${schemacrawler.outputformat}"
   *            alias="schemacrawler.outputformat" default-value="text"
   */
  private String outputFormat;

  /**
   * Output file.
   * 
   * @parameter expression="${schemacrawler.outputfile}"
   *            alias="schemacrawler.outputfile"
   *            default-value="schemacrawler.report.html"
   */
  private String outputFile;

  /**
   * Whether to append to the output.
   * 
   * @parameter expression="${schemacrawler.append}"
   *            alias="schemacrawler.append" default-value="false"
   */
  private boolean append;

  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#getProject()
   */
  protected MavenProject getProject()
  {
    return project;
  }

  /**
   * @see org.apache.maven.reporting.AbstractMavenReport#getSiteRenderer()
   */
  protected Renderer getSiteRenderer()
  {
    return siteRenderer;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#canGenerateReport()
   */
  public boolean canGenerateReport()
  {
    // TODO: Test database connection?
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#generate(org.codehaus.doxia.sink.Sink,
   *      java.util.Locale)
   */
  public void generate(final Sink sink, final Locale locale)
    throws MavenReportException
  {
    executeReport(locale);
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#getCategoryName()
   */
  public String getCategoryName()
  {
    return CATEGORY_PROJECT_REPORTS;
  }

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
   * @see org.apache.maven.reporting.AbstractMavenReport#getOutputDirectory()
   */
  protected String getOutputDirectory()
  {
    return (new File(outputFile)).getParentFile().getAbsolutePath();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#getOutputName()
   */
  public String getOutputName()
  {
    return (new File(outputFile)).getName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#getReportOutputDirectory()
   */
  public File getReportOutputDirectory()
  {
    return (new File(outputFile)).getParentFile();
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#isExternalReport()
   */
  public boolean isExternalReport()
  {
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see org.apache.maven.reporting.MavenReport#setReportOutputDirectory(java.io.File)
   */
  public void setReportOutputDirectory(final File directory)
  {
    // Get the output filename
    final String outputFilename = (new File(outputFile)).getName();
    // Set the new path
    if (directory.exists() && directory.isDirectory())
    {
      outputFile = (new File(directory, outputFilename)).getAbsolutePath();
    }
  }

  public void execute()
    throws MojoExecutionException
  {
    final String errorMessage = "An error has occurred in "
                                + getName(Locale.ENGLISH)
                                + " report generation.";
    try
    {
      final String outputDirectory = getOutputDirectory();
      final SiteRendererSink sink = siteRenderer.createSink(new File(
          outputDirectory), getOutputName() + ".html");
      generate(sink, Locale.getDefault());
    }
    catch (final RendererException e)
    {
      throw new MojoExecutionException(errorMessage, e);
    }
    catch (final IOException e)
    {
      throw new MojoExecutionException(errorMessage, e);
    }
    catch (final MavenReportException e)
    {
      throw new MojoExecutionException(errorMessage, e);
    }
  }

  protected void executeReport(final Locale locale)
    throws MavenReportException
  {

    // Build command line
    final String[] args = new String[] {
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
    final String commandLine = schemacrawler.Main.class + " " + toString(args);
    LOGGER.log(Level.CONFIG, commandLine);
    getLog().info(commandLine);
    try
    {
      schemacrawler.Main.main(args);
    }
    catch (final Exception e)
    {
      throw new MavenReportException("Error executing: " + commandLine, e);
    }
  }

  private String toString(final String[] args)
  {
    final StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < args.length; i++)
    {
      buffer.append(args[i]).append(" ");
    }
    return buffer.toString();
  }

}
