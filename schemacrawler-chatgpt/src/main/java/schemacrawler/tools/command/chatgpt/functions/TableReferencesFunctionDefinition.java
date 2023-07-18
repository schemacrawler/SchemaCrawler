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
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.utility.MetaDataUtility;

public final class TableReferencesFunctionDefinition
    extends AbstractFunctionDefinition<TableReferencesFunctionParameters> {

  public TableReferencesFunctionDefinition() {
    super(
        "Gets the relationships of a database table, either child tables or parent tables. "
            + "Child tables are also known as referencing tables or foreign key tables. "
            + "Parent tables are also known as referenced tables, or primary key tables.",
        TableReferencesFunctionParameters.class);
  }

  @Override
  public Function<TableReferencesFunctionParameters, FunctionReturn> getExecutor() {
    return args -> {
      // Re-filter catalog
      MetaDataUtility.reduceCatalog(catalog, SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions());

      final Optional<Table> firstMatchedTable =
          catalog.getTables().stream()
              .filter(table -> table.getName().matches("(?i)" + args.getTableName()))
              .findFirst();

      if (firstMatchedTable.isPresent()) {
        final Table table = firstMatchedTable.get();
        return new TableReferencesFunctionReturn(table, args.getTableReferenceType());
      } else {
        return new NoResultsReturn();
      }
    };
  }
}
