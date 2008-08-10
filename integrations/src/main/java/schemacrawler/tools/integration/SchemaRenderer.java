/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

import javax.sql.DataSource;

import schemacrawler.schema.Catalog;
import schemacrawler.utility.SchemaCrawlerUtility;

/**
 * An executor that uses a template renderer to render a schema.
 * 
 * @author sfatehi
 */
public abstract class SchemaRenderer
  extends SchemaExecutable
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
    // Get the entire schema at once
    schemaCrawlerOptions.setSchemaInfoLevel(toolOptions
      .getSchemaTextDetailType().mapToInfoLevel());
    Catalog catalog = SchemaCrawlerUtility.getCatalog(dataSource
      .getConnection(), schemaCrawlerOptions);

    // Executable-specific work
    final Writer writer = toolOptions.getOutputOptions().openOutputWriter();
    final String templateName = toolOptions.getOutputOptions()
      .getOutputFormatValue();
    render(templateName, catalog, writer);
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
    executeOnSchema(args, "/schemacrawler-templating-readme.txt");
  }

  /**
   * Renders the schema with the given template.
   * 
   * @param resource
   *        Location of the resource
   * @param catalog
   *        Catalog
   * @param writer
   *        Writer
   * @throws Exception
   */
  protected abstract void render(final String resource,
                                 final Catalog catalog,
                                 final Writer writer)
    throws Exception;

}
