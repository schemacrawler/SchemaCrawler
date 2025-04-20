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

package schemacrawler.schemacrawler;

import static us.fatehi.utility.TemplatingUtility.expandTemplate;
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
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.inclusionrule.InclusionRuleWithRegularExpression;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.utility.MetaDataUtility;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.IOUtility;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
public final class QueryUtility {

  private static final Logger LOGGER = Logger.getLogger(QueryUtility.class.getName());

  public static ResultSet executeAgainstColumnDataType(
      final Query query, final Statement statement, final ColumnDataType columnDataType)
      throws SQLException {
    requireNonNull(query, "No query provided");
    final Map<String, String> variablesMap = makeVariablesMap(columnDataType);
    final String sql = expandQuery(query, variablesMap);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", query.getName(), sql));
    return executeSql(statement, sql);
  }

  public static ResultSet executeAgainstSchema(
      final Query query, final Statement statement, final Map<String, InclusionRule> limitMap)
      throws SQLException {
    requireNonNull(query, "No query provided");
    final Map<String, String> variablesMap = makeVariablesMap(limitMap);
    final String sql = expandQuery(query, variablesMap);
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
    final Map<String, String> variablesMap =
        makeVariablesMap(table, isAlphabeticalSortForTableColumns, identifiers);
    final String sql = expandQuery(query, variablesMap);
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
    final Map<String, String> variablesMap = makeVariablesMap(table, true, identifiers);
    final String sql = expandQuery(query, variablesMap);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", query.getName(), sql));
    return executeSqlForLong(connection, sql);
  }

  public static Object executeForScalar(final Query query, final Connection connection)
      throws SQLException {
    requireNonNull(query, "No query provided");
    final String sql = expandQuery(query);
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
    final Map<String, String> variablesMap = makeVariablesMap(table, true, identifiers);
    final String sql = expandQuery(query, variablesMap);
    LOGGER.log(Level.FINE, new StringFormat("Executing %s: %n%s", query.getName(), sql));
    return executeSqlForScalar(connection, sql);
  }

  public static Query getQueryFromResource(final String name, final String resource) {
    final String sql = IOUtility.readResourceFully(resource);
    return new Query(name, sql);
  }

  protected static void addInclusionRule(
      final String limitType,
      final InclusionRule inclusionRule,
      final Map<String, String> properties) {
    properties.put(limitType, ".*");
    if (inclusionRule instanceof InclusionRuleWithRegularExpression) {
      final String schemaInclusionPattern =
          ((InclusionRuleWithRegularExpression) inclusionRule).getInclusionPattern().pattern();
      if (!isBlank(schemaInclusionPattern)) {
        properties.put(limitType, schemaInclusionPattern);
      }
    }
  }

  private static String expandQuery(final Query query) {
    return expandQuery(query, null);
  }

  private static String expandQuery(final Query query, final Map<String, String> variablesMap) {
    String sql = query.getQuery();
    if (variablesMap != null && !variablesMap.isEmpty()) {
      sql = expandTemplate(sql, variablesMap);
    }
    return expandTemplate(sql);
  }

  private static Map<String, String> makeVariablesMap(final ColumnDataType columnDataType) {
    requireNonNull(columnDataType, "No column data type provided");

    final Map<String, String> variablesMap = new HashMap<>();
    variablesMap.put("column-data-type", columnDataType.getName());
    return variablesMap;
  }

  private static Map<String, String> makeVariablesMap(final Map<String, InclusionRule> limitMap) {
    requireNonNull(limitMap, "No limit map provided");

    final Map<String, String> variablesMap = new HashMap<>();

    for (final Entry<String, InclusionRule> limit : limitMap.entrySet()) {
      addInclusionRule(limit.getKey(), limit.getValue(), variablesMap);
    }

    return variablesMap;
  }

  private static Map<String, String> makeVariablesMap(
      final Table table,
      final boolean isAlphabeticalSortForTableColumns,
      final Identifiers identifiers) {
    requireNonNull(identifiers, "No identifiers provided");

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

    return tableProperties;
  }

  private QueryUtility() {
    // Prevent instantiation
  }
}
