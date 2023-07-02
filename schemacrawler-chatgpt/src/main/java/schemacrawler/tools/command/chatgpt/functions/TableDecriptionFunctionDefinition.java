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

import java.util.Optional;
import java.util.function.Function;
import schemacrawler.schema.Table;

public final class TableDecriptionFunctionDefinition
    extends AbstractFunctionDefinition<TableDecriptionFunctionParameters> {

  public TableDecriptionFunctionDefinition() {
    super(
        "get-table-decription",
        "Gets the details and description of a database table, including columns, foreign keys, indexes and triggers.",
        TableDecriptionFunctionParameters.class);
  }

  @Override
  public Function<TableDecriptionFunctionParameters, FunctionReturn> getExecutor() {
    return args -> {
      final String regex = String.format("(?i).*%s.*", args.getTableNameContains());
      final Optional<Table> firstMatchedTable =
          catalog.getTables().stream().filter(table -> table.getName().matches(regex)).findFirst();

      if (firstMatchedTable.isPresent()) {
        final Table table = firstMatchedTable.get();
        return new TableDescriptionFunctionReturn(table, args.getDescriptionScope());
      } else {
        return new NoResultsReturn();
      }
    };
  }
}
