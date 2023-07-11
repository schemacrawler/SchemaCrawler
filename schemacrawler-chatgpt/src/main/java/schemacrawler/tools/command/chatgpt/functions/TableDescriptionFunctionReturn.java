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

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.COLUMNS;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.DEFAULT;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.FOREIGN_KEYS;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.INDEXES;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.PRIMARY_KEY;
import static schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope.TRIGGERS;
import java.io.StringWriter;
import java.util.Collection;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.LimitOptionsBuilder;
import schemacrawler.schemacrawler.LoadOptionsBuilder;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionParameters.TableDescriptionScope;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.executable.SchemaCrawlerExecutable;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;

public class TableDescriptionFunctionReturn implements FunctionReturn {

  private final Collection<Table> tables;
  private final TableDescriptionScope scope;

  protected TableDescriptionFunctionReturn(
      final Collection<Table> tables, final TableDescriptionScope scope) {
    this.tables = requireNonNull(tables, "Table not provided");
    this.scope = requireNonNull(scope, "Table description scope not provided");
  }

  @Override
  public String render() {
    final Catalog catalog = new CatalogWrapper(tables);

    // Create the options
    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    final LoadOptionsBuilder loadOptionsBuilder = LoadOptionsBuilder.builder();
    final SchemaCrawlerOptions options =
        SchemaCrawlerOptionsBuilder.newSchemaCrawlerOptions()
            .withLimitOptions(limitOptionsBuilder.toOptions())
            .withLoadOptions(loadOptionsBuilder.toOptions());

    final SchemaTextOptionsBuilder schemaTextOptionsBuilder = SchemaTextOptionsBuilder.builder();
    if (scope != DEFAULT) {
      if (scope != COLUMNS) {
        schemaTextOptionsBuilder.noTableColumns();
      }
      if (scope != PRIMARY_KEY) {
        schemaTextOptionsBuilder.noPrimaryKeys();
      }
      if (scope != FOREIGN_KEYS) {
        schemaTextOptionsBuilder.noForeignKeys();
        schemaTextOptionsBuilder.noWeakAssociations();
      }
      if (scope != INDEXES) {
        schemaTextOptionsBuilder.noIndexes();
      }
      if (scope != TRIGGERS) {
        schemaTextOptionsBuilder.noTriggers();
      }
    }
    schemaTextOptionsBuilder.noTableConstraints();
    schemaTextOptionsBuilder.noAlternateKeys();
    final Config config = schemaTextOptionsBuilder.toConfig();

    final StringWriter writer = new StringWriter();
    final OutputOptions outputOptions =
        OutputOptionsBuilder.builder().withOutputWriter(writer).toOptions();

    final String command = "schema";

    final SchemaCrawlerExecutable executable = new SchemaCrawlerExecutable(command);
    executable.setSchemaCrawlerOptions(options);
    executable.setOutputOptions(outputOptions);
    executable.setAdditionalConfiguration(config);
    executable.setCatalog(catalog);
    executable.execute();

    return writer.toString();
  }
}
