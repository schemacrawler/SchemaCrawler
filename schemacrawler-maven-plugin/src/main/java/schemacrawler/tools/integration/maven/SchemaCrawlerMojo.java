package schemacrawler.tools.integration.maven;


import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * _@phase process-sources
 * 
 * @goal schemacrawler
 */
public class SchemaCrawlerMojo
  extends AbstractMojo
{

  private static final Logger LOGGER = Logger.getLogger(SchemaCrawlerMojo.class
    .getName());

  /**
   * Config file.
   * 
   * @parameter expression="${schemacrawler.config}"
   * @required
   */
  private String config;

  /**
   * Config override file.
   * 
   * @parameter expression="${schemacrawler.config-override}"
   */
  private String configOverride;

  /**
   * Datasource.
   * 
   * @parameter expression="${schemacrawler.datasource}"
   * @required
   */
  private String datasource;

  /**
   * Command.
   * 
   * @parameter expression="${schemacrawler.command}"
   * @required
   */
  private String command;

  /**
   * Whether the header should be suppressed.
   * 
   * @parameter expression="${schemacrawler.no-header}"
   */
  private boolean noHeader;

  /**
   * Whether the footer should be suppressed.
   * 
   * @parameter expression="${schemacrawler.no-footer}"
   */
  private boolean noFooter;

  /**
   * Whether the info should be suppressed.
   * 
   * @parameter expression="${schemacrawler.no-info}"
   */
  private boolean noInfo;

  /**
   * Output format.
   * 
   * @parameter expression="${schemacrawler.outputformat}"
   */
  private String outputFormat;

  /**
   * Output file.
   * 
   * @parameter expression="${schemacrawler.outputfile}"
   * @required
   */
  private String outputFile;

  /**
   * Whether to append to the output.
   * 
   * @parameter expression="${schemacrawler.append}"
   */
  private boolean append;

  public void execute()
    throws MojoExecutionException
  {

    // Set defaults
    config = defaulted(config, "schemacrawler.config.properties");
    configOverride = defaulted(configOverride,
                               "schemacrawler.config.override.properties");
    outputFormat = defaulted(outputFormat, "text");

    // Build command line
    String[] args = new String[] {
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
    String commandLine = schemacrawler.Main.class + " " + toString(args);
    LOGGER.log(Level.CONFIG, commandLine);
    try
    {
      schemacrawler.Main.main(args);
    }
    catch (Exception e)
    {
      throw new MojoExecutionException("Error executing: " + commandLine, e);
    }
  }

  private String toString(String[] args)
  {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < args.length; i++)
    {
      buffer.append(args[i]).append(" ");
    }
    return buffer.toString();
  }

  private String defaulted(String parameter, String defaultValue)
  {
    if (parameter == null || parameter.trim().length() == 0)
    {
      return defaultValue;
    }
    else
    {
      return parameter;
    }
  }

}
