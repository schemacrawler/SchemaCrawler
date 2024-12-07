/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.text.operation;

import static schemacrawler.schemacrawler.IdentifierQuotingStrategy.quote_all;
import static schemacrawler.schemacrawler.QueryUtility.executeAgainstTable;
import static us.fatehi.utility.database.DatabaseUtility.createStatement;
import static us.fatehi.utility.database.DatabaseUtility.executeSql;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.exceptions.DatabaseAccessException;
import schemacrawler.tools.command.text.operation.options.Operation;
import schemacrawler.tools.command.text.operation.options.OperationOptions;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.text.formatter.operation.DataTextFormatter;
import schemacrawler.tools.traversal.DataTraversalHandler;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.string.StringFormat;

public final class OperationCommand extends BaseSchemaCrawlerCommand<OperationOptions> {
  private static final Logger LOGGER = Logger.getLogger(OperationCommand.class.getName());

  public OperationCommand(final String command) {
    super(command);
  }

  @Override
  public void checkAvailability() {
    // Operations are always available
  }

  @Override
  public void execute() {
    checkCatalog();

    if (!isOutputFormatSupported()) {
      LOGGER.log(
          Level.INFO,
          new StringFormat(
              "Output format <%s> not supported for command <%s>",
              outputOptions.getOutputFormatValue(), getCommand()));
      return;
    }

    final DataTraversalHandler handler = getDataTraversalHandler();
    final Query query = commandOptions.getQuery();

    handler.begin();

    handler.handleHeaderStart();
    handler.handleHeader(catalog.getCrawlInfo());
    handler.handleHeaderEnd();

    if (query.isQueryOver()) {
      // This is a special instance of identifiers that does not use
      // the configuration from the SchemaCrawler configuration
      // properties file, since the database always needs identifiers
      // to be quoted in SQL queries if they contain spaces in the
      // name
      final String identifierQuoteString = identifiers.getIdentifierQuoteString();
      final Identifiers identifiers =
          IdentifiersBuilder.builder()
              .withIdentifierQuoteString(identifierQuoteString)
              .withIdentifierQuotingStrategy(quote_all)
              .toOptions();

      try (final Statement statement = createStatement(connection)) {
        for (final Table table : getSortedTables(catalog)) {
          final boolean isAlphabeticalSortForTableColumns =
              commandOptions.isAlphabeticalSortForTableColumns();
          try (final ResultSet results =
              executeAgainstTable(
                  query, statement, table, isAlphabeticalSortForTableColumns, identifiers)) {
            handler.handleData(table, results);
          } catch (final SQLException e) {
            LOGGER.log(Level.WARNING, e, new StringFormat("Bad operation for table <%s>", table));
          }
        }
      } catch (final SQLException e) {
        throw new DatabaseAccessException(String.format("Could not run query %n%s%n", query), e);
      }
    } else {
      final String sql = query.getQuery();
      try (final Statement statement = createStatement(connection);
          final ResultSet results = executeSql(statement, sql)) {
        handler.handleData(query, results);
      } catch (final SQLException e) {
        throw new DatabaseAccessException(String.format("Could not run query %n%s%n", query), e);
      }
    }

    handler.end();
  }

  @Override
  public boolean usesConnection() {
    return true;
  }

  private DataTraversalHandler getDataTraversalHandler() {
    final Operation operation = commandOptions.getOperation();

    final DataTraversalHandler formatter =
        new DataTextFormatter(operation, commandOptions, outputOptions, identifiers);
    return formatter;
  }

  private List<? extends Table> getSortedTables(final Catalog catalog) {
    final List<? extends Table> tables = new ArrayList<>(catalog.getTables());
    tables.sort(NamedObjectSort.getNamedObjectSort(commandOptions.isAlphabeticalSortForTables()));
    return tables;
  }

  private boolean isOutputFormatSupported() {
    final String outputFormatValue = outputOptions.getOutputFormatValue();
    final boolean isOutputFormatSupported = TextOutputFormat.isSupportedFormat(outputFormatValue);
    return isOutputFormatSupported;
  }
}
