/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import schemacrawler.tools.options.OutputOptions;

/** A SchemaCrawler tools executable unit. */
public interface SchemaCrawlerCommand<C extends CommandOptions> {

  /**
   * Checks whether a command is available, and throws a runtime exception if it is not available.
   */
  void checkAvailability();

  /**
   * Executes functionality for SchemaCrawler, after database metadata has been obtained. May throw
   * runtime exceptions on errors.
   */
  void execute();

  Catalog getCatalog();

  String getCommand();

  C getCommandOptions();

  Connection getConnection();

  Identifiers getIdentifiers();

  OutputOptions getOutputOptions();

  SchemaCrawlerOptions getSchemaCrawlerOptions();

  /** Initializes the command for execution. */
  void initialize();

  void setCatalog(Catalog catalog);

  void setCommandOptions(C commandOptions);

  void setConnection(Connection connection);

  void setIdentifiers(Identifiers identifiers);

  void setOutputOptions(OutputOptions outputOptions);

  void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions);

  default boolean usesConnection() {
    return false;
  }
}
