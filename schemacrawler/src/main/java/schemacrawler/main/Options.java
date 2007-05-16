/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.main;


import java.util.logging.Level;

import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.ToolType;
import schemacrawler.tools.datatext.DataTextFormatOptions;
import schemacrawler.tools.operation.OperatorOptions;
import schemacrawler.tools.schematext.SchemaTextOptions;

/**
 * Program options.
 * 
 * @author sfatehi
 */
public final class Options
{

  private static final String SCHEMACRAWLER_LOG_LEVEL = "schemacrawler.log_level";

  private final Config config;
  private final SchemaCrawlerOptions schemaCrawlerOptions;
  private final ToolType toolType;
  private final SchemaTextOptions schemaTextOptions;
  private final DataTextFormatOptions dataTextFormatOptions;
  private final OperatorOptions operatorOptions;
  private final String query;
  private Level logLevel;

  /**
   * Parses options from a properties file.
   * 
   * @param config
   *        Properties
   * @param command
   *        Properties
   * @param pageOptions
   *        Page options
   */
  Options(final Config config,
          final Command command,
          final OutputOptions outputOptions)
  {
    if (config == null || command == null || outputOptions == null)
    {
      throw new IllegalArgumentException("All arguments need to be specified");
    }

    this.config = config;

    schemaCrawlerOptions = new SchemaCrawlerOptions(config);

    schemaTextOptions = new SchemaTextOptions(config, outputOptions, command
      .getSchemaTextDetailType());
    dataTextFormatOptions = new DataTextFormatOptions(config, outputOptions);
    operatorOptions = new OperatorOptions(outputOptions,
                                          command.getOperation(),
                                          command.getQuery());

    toolType = command.getToolType();
    query = command.getQuery();

    // Set additional options from the config
    setLogLevel(config.getStringValue(SCHEMACRAWLER_LOG_LEVEL, "OFF"));
  }

  /**
   * Gets the configuration properties.
   * 
   * @return Configuration properties
   */
  public Config getConfig()
  {
    return new Config(config);
  }

  /**
   * Gets the data text format options.
   * 
   * @return Data text format options
   */
  public DataTextFormatOptions getDataTextFormatOptions()
  {
    return dataTextFormatOptions;
  }

  /**
   * Log level.
   * 
   * @return Log level
   */
  public Level getLogLevel()
  {
    return logLevel;
  }

  /**
   * Gets the operator options.
   * 
   * @return Operator options
   */
  public OperatorOptions getOperatorOptions()
  {
    return operatorOptions;
  }

  /**
   * Query.
   * 
   * @return Query
   */
  public String getQuery()
  {
    return query;
  }

  /**
   * Gets the schema crawler options.
   * 
   * @return S
   */
  public SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  /**
   * Get schema text options.
   * 
   * @return Schema text options
   */
  public SchemaTextOptions getSchemaTextOptions()
  {
    return schemaTextOptions;
  }

  /**
   * Gets the tool type.
   * 
   * @return Tool type
   */
  public ToolType getToolType()
  {
    return toolType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("Options[");
    buffer.append("query=").append(query);
    buffer.append("; log level=").append(logLevel.getName());
    buffer.append("; ").append(schemaCrawlerOptions);
    buffer.append("; ").append(schemaTextOptions);
    buffer.append("; ").append(dataTextFormatOptions);
    buffer.append("; ").append(operatorOptions);
    buffer.append("]");
    return buffer.toString();
  }

  void setLogLevel(final String logLevelString)
  {
    logLevel = Level.parse(logLevelString.toUpperCase());
  }

}
