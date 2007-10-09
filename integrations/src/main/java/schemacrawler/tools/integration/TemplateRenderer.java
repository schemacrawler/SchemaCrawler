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
package schemacrawler.tools.integration;


import java.io.Writer;
import java.util.List;

import javax.sql.DataSource;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.SchemaCrawlerOptions;
import schemacrawler.main.CommandParser;
import schemacrawler.main.ConfigParser;
import schemacrawler.main.OutputOptionsParser;
import schemacrawler.schema.Schema;
import schemacrawler.tools.Command;
import schemacrawler.tools.Executable;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.schematext.SchemaTextDetailType;
import schemacrawler.tools.schematext.SchemaTextOptions;
import sf.util.CommandLineUtility;
import sf.util.Config;
import dbconnector.dbconnector.DatabaseConnector;
import dbconnector.dbconnector.DatabaseConnectorFactory;

/**
 * An executor that uses a template renderer to render a schema.
 * 
 * @author sfatehi
 */
public abstract class TemplateRenderer
  extends Executable<SchemaTextOptions>
{

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.tools.Executable#execute(javax.sql.DataSource)
   */
  @Override
  public void execute(final DataSource dataSource)
    throws Exception
  {
    // Get the entire schema at once, since we need to use this to
    // render the template
    final Schema schema = SchemaCrawler.getSchema(dataSource, toolOptions
      .getSchemaTextDetailType().mapToInfoLevel(), schemaCrawlerOptions);
    final Writer writer = toolOptions.getOutputOptions().openOutputWriter();
    final String templateName = toolOptions.getOutputOptions()
      .getOutputFormatValue();
    renderTemplate(templateName, schema, writer);
    toolOptions.getOutputOptions().closeOutputWriter(writer);
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  public void main(final String[] args)
    throws Exception
  {
    CommandLineUtility.checkForHelp(args,
                                    "/schemacrawler-templating-readme.txt");
    CommandLineUtility.setLogLevel(args);

    final Config config = ConfigParser.parseCommandLine(args);
    final DatabaseConnector dataSourceParser = DatabaseConnectorFactory
      .createPropertiesDriverDataSourceParser(args, config);

    final SchemaCrawlerOptions schemaCrawlerOptions = new SchemaCrawlerOptions(config);
    final OutputOptions outputOptions = OutputOptionsParser
      .parseOutputOptions(args);

    final List<Command> commands = CommandParser.parseCommands(args);
    final SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType
      .valueOf(commands.get(0).getName());

    final SchemaTextOptions schemaTextOptions = new SchemaTextOptions(config,
                                                                      outputOptions,
                                                                      schemaTextDetailType);

    setSchemaCrawlerOptions(schemaCrawlerOptions);
    setToolOptions(schemaTextOptions);
    execute(dataSourceParser.createDataSource());
  }

  /**
   * Renders the schema with the given template.
   * 
   * @param templateName
   *        Name of the template
   * @param schema
   *        Schema
   * @param writer
   *        Writer
   * @throws Exception
   */
  protected abstract void renderTemplate(final String templateName,
                                         final Schema schema,
                                         final Writer writer)
    throws Exception;

}
