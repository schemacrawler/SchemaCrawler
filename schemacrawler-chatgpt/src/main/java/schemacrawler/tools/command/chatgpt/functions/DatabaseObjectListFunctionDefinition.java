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

package schemacrawler.tools.command.chatgpt.functions;

import java.util.function.Function;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType;

public final class DatabaseObjectListFunctionDefinition
    extends AbstractFunctionDefinition<DatabaseObjectListFunctionParameters> {

  public DatabaseObjectListFunctionDefinition() {
    super(
        "database-object-list",
        "Lists database objects like tables, routines (that is, functions and stored procedures), schemas (that is, catalogs), sequences, or synonyms.",
        DatabaseObjectListFunctionParameters.class);
  }

  @Override
  public Function<DatabaseObjectListFunctionParameters, FunctionReturn> getExecutor() {
    return args -> {
      final DatabaseObjectType databaseObjectType = args.getDatabaseObjectType();
      switch (databaseObjectType) {
        case SCHEMAS:
          return new DatabaseObjectListFunctionReturn(databaseObjectType, catalog.getSchemas());
        case ROUTINES:
          return new DatabaseObjectListFunctionReturn(databaseObjectType, catalog.getRoutines());
        case SEQUENCES:
          return new DatabaseObjectListFunctionReturn(databaseObjectType, catalog.getSequences());
        case SYNONYMS:
          return new DatabaseObjectListFunctionReturn(databaseObjectType, catalog.getSynonyms());
        case TABLES:
          // fall-through
        default:
          return new DatabaseObjectListFunctionReturn(databaseObjectType, catalog.getTables());
      }
    };
  }
}
