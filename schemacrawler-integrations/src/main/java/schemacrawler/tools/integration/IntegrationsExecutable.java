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


import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.Executable;
import schemacrawler.tools.ExecutionException;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;

/**
 * Basic SchemaCrawler integrations executor.
 * 
 * @author Sualeh Fatehi
 */
public abstract class IntegrationsExecutable
  extends Executable<SchemaTextOptions>
{

  @Override
  public void initializeToolOptions(final String command,
                                    final Config config,
                                    final OutputOptions outputOptions)
    throws ExecutionException
  {
    toolOptions = new SchemaTextOptions(config,
                                        outputOptions,
                                        SchemaTextDetailType.standard_schema);
  }

}
