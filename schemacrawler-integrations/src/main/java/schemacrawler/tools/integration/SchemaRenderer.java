/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.tools.executable.BaseExecutable;

/**
 * An executor that uses a template renderer to render a schema.
 * 
 * @author sfatehi
 */
public abstract class SchemaRenderer
  extends BaseExecutable
{

  private static final long serialVersionUID = 7441125886947849708L;

  protected SchemaRenderer(final String command)
  {
    super(command);
  }

  @Override
  public final void executeOn(final Database database,
                              final Connection connection)
    throws Exception
  {
    final Writer writer = outputOptions.openOutputWriter();
    final String resource = outputOptions.getOutputFormatValue();
    render(database, connection, resource, writer);
    outputOptions.closeOutputWriter(writer);
  }

  /**
   * Renders the schema with the given template.
   * 
   * @param database
   *        Database
   * @param resource
   *        Location of the resource
   * @param writer
   *        Writer
   * @throws Exception
   */
  protected abstract void render(Database database,
                                 Connection connection,
                                 String resource,
                                 Writer writer)
    throws Exception;

}
