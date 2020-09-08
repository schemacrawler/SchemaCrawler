/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.executable;


import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;

/**
 * A SchemaCrawler tools executable unit.
 *
 * @author Sualeh Fatehi
 */
public interface SchemaCrawlerCommand
{

  /**
   * Checks whether a command is available, and throws an exception if it is not
   * available.
   *
   * @throws Exception
   *   On an exception
   */
  void checkAvailability()
    throws Exception;

  /**
   * Executes functionality for SchemaCrawler, after database metadata has been
   * obtained.
   *
   * @throws Exception
   *   On an exception
   */
  void execute()
    throws Exception;

  Config getAdditionalConfiguration();

  void setAdditionalConfiguration(Config config);

  Catalog getCatalog();

  void setCatalog(Catalog catalog);

  String getCommand();

  Connection getConnection();

  void setConnection(Connection connection);

  Identifiers getIdentifiers();

  void setIdentifiers(Identifiers identifiers);

  OutputOptions getOutputOptions();

  void setOutputOptions(OutputOptions outputOptions);

  SchemaCrawlerOptions getSchemaCrawlerOptions();

  void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions);

  /**
   * Initializes the command for execution.
   *
   * @throws Exception
   *   On an exception
   */
  void initialize()
    throws Exception;

  default boolean usesConnection()
  {
    return false;
  }

}
