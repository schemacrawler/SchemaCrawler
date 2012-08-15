/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.tools.integration.scripting;


import java.io.File;
import java.sql.Connection;

import schemacrawler.schema.Database;
import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.Executable;
import schemacrawler.tools.executable.ExecutableChain;

public class CommandsChain
  extends ExecutableChain
{

  private SchemaCrawlerOptions schemaCrawlerOptions;
  private Config additionalConfiguration;

  protected CommandsChain(final SchemaCrawlerOptions schemaCrawlerOptions,
                          final Config additionalConfiguration,
                          final Database database,
                          final Connection connection)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
    this.additionalConfiguration = additionalConfiguration;
    setDatabase(database);
    setConnection(connection);
  }

  public Executable addNext(final String command,
                            final String outputFormat,
                            final String outputFileName)
    throws SchemaCrawlerException
  {
    return addNext(command,
                   schemaCrawlerOptions,
                   additionalConfiguration,
                   outputFormat,
                   new File(outputFileName));
  }

  protected Config getAdditionalConfiguration()
  {
    return additionalConfiguration;
  }

  protected SchemaCrawlerOptions getSchemaCrawlerOptions()
  {
    return schemaCrawlerOptions;
  }

  protected void setAdditionalConfiguration(final Config additionalConfiguration)
  {
    this.additionalConfiguration = additionalConfiguration;
  }

  protected void setSchemaCrawlerOptions(final SchemaCrawlerOptions schemaCrawlerOptions)
  {
    this.schemaCrawlerOptions = schemaCrawlerOptions;
  }

}
