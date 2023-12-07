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

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.TemplatingUtility.expandTemplate;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.database.DatabaseUtility.executeSql;
import static us.fatehi.utility.database.DatabaseUtility.executeSqlForLong;
import static us.fatehi.utility.database.DatabaseUtility.executeSqlForScalar;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.InclusionRuleWithRegularExpression;
import schemacrawler.schema.Column;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public final class QueryUtility {

  private static final Logger LOGGER = Logger.getLogger(QueryUtility.class.getName());

  public static ResultSet executeAgainstSchema(
      final Query query,
      final Statement statement,
      final InclusionRule schemaInclusionRule,
      final InclusionRule tableInclusionRule)
      throws SQLException {
    requireNonNull(query, "No query provided");
    final String sql = getQuery(query, schemaInclusionRule);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", query.getName(), sql));
    return executeSql(statement, sql);
  }

  public static ResultSet executeAgainstTable(
      final Query query,
      final Statement statement,
      final Table table,
      final boolean isAlphabeticalSortForTableColumns,
      final Identifiers identifiers)
      throws SQLException {
    requireNonNull(query, "No query provided");
    requireNonNull(identifiers, "No identifiers provided");

    final String sql = getQuery(query, table, isAlphabeticalSortForTableColumns, identifiers);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", query.getName(), sql));
    return executeSql(statement, sql);
  }

  public static long executeForLong(
      final Query query,
      final Connection connection,
      final Table table,
      final Identifiers identifiers)
      throws SQLException {
    requireNonNull(query, "No query provided");
    final String sql = getQuery(query, table, true, identifiers);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", query.getName(), sql));
    return executeSqlForLong(connection, sql);
  }

  public static Object executeForScalar(final Query query, final Connection connection)
      throws SQLException {
    requireNonNull(query, "No query provided");
    final String sql = getQuery(query);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", query.getName(), sql));
    return executeSqlForScalar(connection, sql);
  }

  public static Object executeForScalar(
      final Query query,
      final Connection connection,
      final Table table,
      final Identifiers identifiers)
      throws SQLException {
    requireNonNull(query, "No query provided");
    final String sql = getQuery(query, table, true, identifiers);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", query.getName(), sql));
    return executeSqlForScalar(connection, sql);
  }

  private static String getQuery(final Query query) {
    return expandTemplate(query.getQuery());
  }

  /**
   * Gets the query with parameters substituted.
   *
   * @param schemaInclusionRule Schema inclusion rule
   * @return Ready-to-execute query
   */
  private static String getQuery(final Query query, final InclusionRule schemaInclusionRule) {
    final Map<String, String> properties = new HashMap<>();

    properties.put("schemas", ".*");
    if (schemaInclusionRule instanceof InclusionRuleWithRegularExpression) {
      final String schemaInclusionPattern =
          ((InclusionRuleWithRegularExpression) schemaInclusionRule)
              .getInclusionPattern()
              .pattern();
      if (!isBlank(schemaInclusionPattern)) {
        properties.put("schemas", schemaInclusionPattern);
      }
    }

    String sql = query.getQuery();
    sql = expandTemplate(sql, properties);
    return expandTemplate(sql);
  }

  private static String getQuery(
      final Query query,
      final Table table,
      final boolean isAlphabeticalSortForTableColumns,
      final Identifiers identifiers) {
    final Map<String, String> tableProperties = new HashMap<>();
    if (table != null) {
      final NamedObjectSort columnsSort =
          NamedObjectSort.getNamedObjectSort(isAlphabeticalSortForTableColumns);
      final List<Column> columns = table.getColumns();
      columns.sort(columnsSort);

      final Schema schema = table.getSchema();
      if (schema != null) {
        final String schemaName = identifiers.quoteFullName(schema);
        tableProperties.put("schema", schemaName);
      }
      tableProperties.put("table", identifiers.quoteFullName(table));
      tableProperties.put("tablename", table.getName());
      tableProperties.put("columns", MetaDataUtility.joinColumns(columns, false, identifiers));
      tableProperties.put(
          "orderbycolumns", MetaDataUtility.joinColumns(columns, true, identifiers));
      tableProperties.put("tabletype", table.getTableType().toString());
    }

    String sql = query.getQuery();
    sql = expandTemplate(sql, tableProperties);
    return expandTemplate(sql);
  }

  private QueryUtility() {
    // Prevent instantiation
  }
}
