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

package schemacrawler.crawl;

import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_SYNONYMS;
import static us.fatehi.utility.Utility.isBlank;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Synonym;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaReference;
import us.fatehi.utility.string.StringFormat;

/**
 * A retriever that uses database metadata to get the extended details about the database synonyms.
 *
 * <p>(Based on an idea from Matt Albrecht)
 */
final class SynonymRetriever extends AbstractRetriever {

  private static final class UnknownDatabaseObject extends AbstractDatabaseObject {

    private static final long serialVersionUID = -2212843304418302122L;

    UnknownDatabaseObject(final Schema schema, final String name) {
      super(schema, name);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(SynonymRetriever.class.getName());

  SynonymRetriever(
      final RetrieverConnection retrieverConnection,
      final MutableCatalog catalog,
      final SchemaCrawlerOptions options)
      throws SQLException {
    super(retrieverConnection, catalog, options);
  }

  /**
   * Retrieves the synonym definitions from the database.
   *
   * @param synonymInclusionRule Rule for including synonyms
   * @throws SQLException On a SQL exception
   */
  void retrieveSynonymInformation(final InclusionRule synonymInclusionRule) throws SQLException {
    final InclusionRuleFilter<Synonym> synonymFilter =
        new InclusionRuleFilter<>(synonymInclusionRule, false);
    if (synonymFilter.isExcludeAll()) {
      LOGGER.log(Level.INFO, "Not retrieving synonyms, since this was not requested");
      return;
    }

    LOGGER.log(Level.INFO, "Retrieving synonyms");

    final InformationSchemaViews informationSchemaViews =
        getRetrieverConnection().getInformationSchemaViews();
    if (!informationSchemaViews.hasQuery(EXT_SYNONYMS)) {
      LOGGER.log(Level.FINE, "Synonym definition SQL statement was not provided");
      return;
    }

    final NamedObjectList<SchemaReference> schemas = getAllSchemas();

    final Query synonymsDefinitionSql = informationSchemaViews.getQuery(EXT_SYNONYMS);
    try (final Connection connection = getRetrieverConnection().getConnection();
        final Statement statement = connection.createStatement();
        final MetadataResultSet results =
            new MetadataResultSet(synonymsDefinitionSql, statement, getLimitMap()); ) {
      while (results.next()) {
        final String catalogName = normalizeCatalogName(results.getString("SYNONYM_CATALOG"));
        final String schemaName = normalizeSchemaName(results.getString("SYNONYM_SCHEMA"));
        final String synonymName = results.getString("SYNONYM_NAME");
        final String referencedObjectCatalogName = results.getString("REFERENCED_OBJECT_CATALOG");
        final String referencedObjectSchemaName = results.getString("REFERENCED_OBJECT_SCHEMA");
        final String referencedObjectName = results.getString("REFERENCED_OBJECT_NAME");

        if (isBlank(referencedObjectName)) {
          LOGGER.log(
              Level.FINE,
              new StringFormat(
                  "No reference for synonym <%s.%s.%s>", catalogName, schemaName, synonymName));
          continue;
        }

        final Schema schema = new SchemaReference(catalogName, schemaName);
        final Schema referencedSchema =
            new SchemaReference(referencedObjectCatalogName, referencedObjectSchemaName);
        if (!schemas.contains(schema) && !schemas.contains(referencedSchema)) {
          continue;
        }

        final Optional<MutableTable> referencedTable =
            lookupTable(
                referencedObjectCatalogName, referencedObjectSchemaName, referencedObjectName);
        final Optional<MutableRoutine> referencedRoutine =
            lookupRoutine(
                referencedObjectCatalogName,
                referencedObjectSchemaName,
                referencedObjectName,
                referencedObjectName);
        final DatabaseObject referencedObject;
        if (referencedTable.isPresent()) {
          referencedObject = referencedTable.get();
        } else if (referencedRoutine.isPresent()) {
          referencedObject = referencedRoutine.get();
        } else {
          referencedObject = new UnknownDatabaseObject(referencedSchema, referencedObjectName);
        }

        final MutableSynonym synonym = new MutableSynonym(schema, synonymName);
        synonym.withQuoting(getRetrieverConnection().getIdentifiers());

        if (synonymFilter.test(synonym)) {
          catalog.addSynonym(synonym);
        }

        synonym.setReferencedObject(referencedObject);

        synonym.addAttributes(results.getAttributes());
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve synonyms", e);
    }
  }
}
