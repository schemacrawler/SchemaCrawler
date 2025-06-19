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

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.VIEWS;
import static schemacrawler.schemacrawler.InformationSchemaKey.VIEW_TABLE_USAGE;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schema.CheckOptionType;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import us.fatehi.utility.string.StringFormat;

/** A retriever uses database metadata to get the extended details about the database tables. */
final class ViewExtRetriever extends AbstractRetriever {

  private static final Logger LOGGER = Logger.getLogger(ViewExtRetriever.class.getName());

  ViewExtRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Retrieves view information from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveViewInformation() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(VIEWS)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional view information, since this was not requested");
      LOGGER.log(Level.FINE, "Views SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving additional view information");

    final RetrievalCounts retrievalCounts = new RetrievalCounts("views for definitions");
    final Query viewInformationSql = informationSchemaViews.getQuery(VIEWS);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(viewInformationSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        // Get the "VIEW_DEFINITION" value first as it the Oracle driver
        // don't handle it properly otherwise.
        // https://github.com/schemacrawler/SchemaCrawler/issues/835
        final String definition = results.getString("VIEW_DEFINITION");

        final String catalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String viewName = results.getString("TABLE_NAME");

        final Optional<MutableTable> viewOptional = lookupTable(catalogName, schemaName, viewName);
        if (!viewOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find table <%s.%s.%s>", catalogName, schemaName, viewName));
          continue;
        }

        final MutableView view = (MutableView) viewOptional.get();
        LOGGER.log(Level.FINER, new StringFormat("Retrieving view information <%s>", viewName));

        final CheckOptionType checkOption =
            results.getEnum("CHECK_OPTION", CheckOptionType.unknown);
        final boolean updatable = results.getBoolean("IS_UPDATABLE");

        view.appendDefinition(definition);
        view.setCheckOption(checkOption);
        view.setUpdatable(updatable);

        view.addAttributes(results.getAttributes());

        retrievalCounts.countIncluded();
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve views", e);
    }
    retrievalCounts.log();
  }

  /**
   * Retrieves view table usage from the database, in the INFORMATION_SCHEMA format.
   *
   * @throws SQLException On a SQL exception
   */
  void retrieveViewTableUsage() throws SQLException {
    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();

    if (!informationSchemaViews.hasQuery(VIEW_TABLE_USAGE)) {
      LOGGER.log(
          Level.INFO, "Not retrieving additional view table usage, since this was not requested");
      LOGGER.log(Level.FINE, "View table usage SQL statement was not provided");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving view table usage");

    final RetrievalCounts retrievalCounts = new RetrievalCounts("views for table usage");
    final Query viewTableUsageSql = informationSchemaViews.getQuery(VIEW_TABLE_USAGE);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(viewTableUsageSql, statement, getLimitMap()); ) {
      while (results.next()) {
        retrievalCounts.count();
        final String catalogName = normalizeCatalogName(results.getString("VIEW_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("VIEW_SCHEMA"));
        final String viewName = results.getString("VIEW_NAME");

        final Optional<MutableTable> viewOptional = lookupTable(catalogName, schemaName, viewName);
        if (!viewOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat("Cannot find view <%s.%s.%s>", catalogName, schemaName, viewName));
          continue;
        }

        final MutableView view = (MutableView) viewOptional.get();
        LOGGER.log(Level.FINER, new StringFormat("Retrieving view information <%s>", viewName));

        final String tableCatalogName = normalizeCatalogName(results.getString("TABLE_CATALOG"));
        final String tableSchemaName = normalizeSchemaName(results.getString("TABLE_SCHEMA"));
        final String tableName = results.getString("TABLE_NAME");

        final Optional<MutableTable> tableOptional =
            lookupTable(tableCatalogName, tableSchemaName, tableName);
        if (!tableOptional.isPresent()) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "Cannot find table <%s.%s.%s>", tableCatalogName, tableSchemaName, tableName));
          continue;
        }

        final MutableTable table = tableOptional.get();
        LOGGER.log(Level.FINER, new StringFormat("Retrieving table information <%s>", tableName));

        view.addTableUsage(table);

        retrievalCounts.countIncluded();
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve table usage for views", e);
    }
    retrievalCounts.log();
  }
}
