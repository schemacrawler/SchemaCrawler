/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.JavaSqlType;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaReference;
import schemacrawler.utility.TypeMap;

/**
 * Base class for retriever that uses database metadata to get the
 * details about the schema.
 *
 * @author Sualeh Fatehi
 */
abstract class AbstractRetriever
  implements Retriever
{

  private static final Logger LOGGER = Logger
    .getLogger(AbstractRetriever.class.getName());

  private final RetrieverConnection retrieverConnection;
  final MutableCatalog catalog;

  AbstractRetriever(final RetrieverConnection retrieverConnection,
                    final MutableCatalog catalog)
    throws SQLException
  {
    this.retrieverConnection = requireNonNull(retrieverConnection,
                                              "No retriever connection provided");
    this.catalog = catalog;
  }

  /**
   * Checks whether the provided database object belongs to the
   * specified schema.
   *
   * @param dbObject
   *        Database object to check
   * @param catalogName
   *        Database catalog to check against
   * @param schemaName
   *        Database schema to check against
   * @return Whether the database object belongs to the specified schema
   */
  boolean belongsToSchema(final DatabaseObject dbObject,
                          final String catalogName,
                          final String schemaName)
  {
    if (dbObject == null)
    {
      return false;
    }

    final boolean supportsCatalogs = retrieverConnection.isSupportsCatalogs();

    boolean belongsToCatalog = true;
    boolean belongsToSchema = true;
    if (supportsCatalogs)
    {
      final String dbObjectCatalogName = dbObject.getSchema().getCatalogName();
      if (catalogName != null && !unquotedName(catalogName)
        .equals(unquotedName(dbObjectCatalogName)))
      {
        belongsToCatalog = false;
      }
    }
    final String dbObjectSchemaName = dbObject.getSchema().getName();
    if (schemaName != null
        && !unquotedName(schemaName).equals(unquotedName(dbObjectSchemaName)))
    {
      belongsToSchema = false;
    }
    return belongsToCatalog && belongsToSchema;
  }

  Connection getDatabaseConnection()
  {
    return retrieverConnection.getConnection();
  }

  DatabaseMetaData getMetaData()
  {
    return retrieverConnection.getMetaData();
  }

  RetrieverConnection getRetrieverConnection()
  {
    return retrieverConnection;
  }

  Collection<Schema> getSchemas()
  {
    return catalog.getSchemas();
  }

  void logSQLFeatureNotSupported(final String message, final Throwable e)
  {
    LOGGER.log(Level.WARNING, message);
    LOGGER.log(Level.FINE, message, e);
  }

  /**
   * Creates a data type from the JDBC data type id, and the database
   * specific type name, if it does not exist.
   *
   * @param schema
   *        Schema
   * @param javaSqlType
   *        JDBC data type
   * @param databaseSpecificTypeName
   *        Database specific type name
   * @return Column data type
   */
  MutableColumnDataType lookupOrCreateColumnDataType(final Schema schema,
                                                     final int javaSqlType,
                                                     final String databaseSpecificTypeName)
  {
    return lookupOrCreateColumnDataType(schema,
                                        javaSqlType,
                                        databaseSpecificTypeName,
                                        null);
  }

  /**
   * Creates a data type from the JDBC data type id, and the database
   * specific type name, if it does not exist.
   *
   * @param schema
   *        Schema
   * @param javaSqlTypeInt
   *        JDBC data type
   * @param databaseSpecificTypeName
   *        Database specific type name
   * @return Column data type
   */
  MutableColumnDataType lookupOrCreateColumnDataType(final Schema schema,
                                                     final int javaSqlTypeInt,
                                                     final String databaseSpecificTypeName,
                                                     final String mappedClassName)
  {
    MutableColumnDataType columnDataType = catalog
      .lookupColumnDataType(schema, databaseSpecificTypeName).orElse(catalog
        .lookupSystemColumnDataType(databaseSpecificTypeName).orElse(null));
    // Create new data type, if needed
    if (columnDataType == null)
    {
      columnDataType = new MutableColumnDataType(schema,
                                                 databaseSpecificTypeName);
      final JavaSqlType javaSqlType = retrieverConnection.getJavaSqlTypes()
        .get(javaSqlTypeInt);
      columnDataType.setJavaSqlType(javaSqlType);
      if (isBlank(mappedClassName))
      {
        final TypeMap typeMap = retrieverConnection.getTypeMap();
        final Class<?> mappedClass;
        if (typeMap.containsKey(databaseSpecificTypeName))
        {
          mappedClass = typeMap.get(databaseSpecificTypeName);
        }
        else
        {
          mappedClass = typeMap.get(javaSqlType.getJavaSqlTypeName());
        }
        columnDataType.setTypeMappedClass(mappedClass);
      }
      else
      {
        columnDataType.setTypeMappedClass(mappedClassName);
      }

      catalog.addColumnDataType(columnDataType);
    }
    return columnDataType;
  }

  Optional<MutableRoutine> lookupRoutine(final String catalogName,
                                         final String schemaName,
                                         final String routineName,
                                         final String specificName)
  {
    final String routineLookupName;
    if (!isBlank(specificName))
    {
      routineLookupName = specificName;
    }
    else
    {
      routineLookupName = routineName;
    }
    return catalog.lookupRoutine(new SchemaReference(catalogName, schemaName),
                                 routineLookupName);
  }

  Optional<MutableTable> lookupTable(final String catalogName,
                                     final String schemaName,
                                     final String tableName)
  {
    return catalog.lookupTable(new SchemaReference(catalogName, schemaName),
                               tableName);
  }

  String quotedName(final String name)
  {
    return retrieverConnection.getIdentifiers().quotedName(name);
  }

  String unquotedName(final String name)
  {
    return retrieverConnection.getIdentifiers().unquotedName(name);
  }

}
