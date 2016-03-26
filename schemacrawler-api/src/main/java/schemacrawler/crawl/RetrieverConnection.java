/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.crawl;


import static sf.util.DatabaseUtility.checkConnection;
import static sf.util.Utility.isBlank;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.utility.Identifiers;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.utility.TableTypes;
import schemacrawler.utility.TypeMap;
import sf.util.StringFormat;

/**
 * A connection for the retriever. Wraps a live database connection.
 *
 * @author Sualeh Fatehi
 */
final class RetrieverConnection
{

  private static final Logger LOGGER = Logger
    .getLogger(RetrieverConnection.class.getName());

  private static String lookupIdentifierQuoteString(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                                                    final DatabaseMetaData metaData)
    throws SQLException
  {
    String identifierQuoteString;
    if (databaseSpecificOverrideOptions != null
        && databaseSpecificOverrideOptions
          .hasOverrideForIdentifierQuoteString())
    {
      identifierQuoteString = databaseSpecificOverrideOptions
        .getIdentifierQuoteString();
    }
    else
    {
      identifierQuoteString = metaData.getIdentifierQuoteString();
    }
    if (isBlank(identifierQuoteString))
    {
      identifierQuoteString = "";
    }

    return identifierQuoteString;
  }

  private static boolean lookupSupportsCatalogs(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                                                final DatabaseMetaData metaData)
    throws SQLException
  {
    final boolean supportsCatalogs;
    if (databaseSpecificOverrideOptions != null
        && databaseSpecificOverrideOptions.hasOverrideForSupportsCatalogs())
    {
      supportsCatalogs = databaseSpecificOverrideOptions.isSupportsCatalogs();
    }
    else
    {
      supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
    }
    return supportsCatalogs;
  }

  private static boolean lookupSupportsSchemas(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                                               final DatabaseMetaData metaData)
    throws SQLException
  {
    final boolean supportsSchemas;
    if (databaseSpecificOverrideOptions != null
        && databaseSpecificOverrideOptions.hasOverrideForSupportsSchemas())
    {
      supportsSchemas = databaseSpecificOverrideOptions.isSupportsSchemas();
    }
    else
    {
      supportsSchemas = metaData.supportsSchemasInTableDefinitions();
    }

    return supportsSchemas;
  }

  private final Connection connection;
  private final DatabaseMetaData metaData;
  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;
  private final MetadataRetrievalStrategy tableColumnRetrievalStrategy;
  private final Identifiers identifiers;
  private final InformationSchemaViews informationSchemaViews;
  private final TableTypes tableTypes;
  private final JavaSqlTypes javaSqlTypes;
  private final TypeMap typeMap;

  RetrieverConnection(final Connection connection,
                      final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions)
    throws SQLException
  {
    try
    {
      checkConnection(connection);
    }
    catch (final SchemaCrawlerException e)
    {
      throw new SQLException("Bad database connection", e);
    }
    this.connection = connection;
    metaData = connection.getMetaData();

    if (databaseSpecificOverrideOptions == null)
    {
      informationSchemaViews = new InformationSchemaViews();
    }
    else
    {
      informationSchemaViews = databaseSpecificOverrideOptions
        .getInformationSchemaViews();
    }

    supportsCatalogs = lookupSupportsCatalogs(databaseSpecificOverrideOptions,
                                              metaData);
    LOGGER
      .log(Level.CONFIG,
           new StringFormat("Database %s catalogs",
                            supportsCatalogs? "supports": "does not support"));

    supportsSchemas = lookupSupportsSchemas(databaseSpecificOverrideOptions,
                                            metaData);
    LOGGER
      .log(Level.CONFIG,
           new StringFormat("Database %s schemas",
                            supportsSchemas? "supports": "does not support"));

    tableColumnRetrievalStrategy = databaseSpecificOverrideOptions
      .getTableColumnRetrievalStrategy();

    final String identifierQuoteString = lookupIdentifierQuoteString(databaseSpecificOverrideOptions,
                                                                     metaData);
    LOGGER.log(Level.CONFIG,
               new StringFormat("Database identifier quote string is \"%s\"",
                                identifierQuoteString));
    identifiers = Identifiers.identifiers().withConnection(connection)
      .withIdentifierQuoteString(identifierQuoteString).build();

    tableTypes = new TableTypes(connection);
    LOGGER.log(Level.CONFIG,
               new StringFormat("Supported table types are %s", tableTypes));

    typeMap = new TypeMap(connection);
    javaSqlTypes = new JavaSqlTypes();
  }

  Connection getConnection()
  {
    return connection;
  }

  Identifiers getIdentifiers()
  {
    return identifiers;
  }

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   *
   * @return INFORMATION_SCHEMA views selects
   */
  InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  JavaSqlTypes getJavaSqlTypes()
  {
    return javaSqlTypes;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  MetadataRetrievalStrategy getTableColumnRetrievalStrategy()
  {
    return tableColumnRetrievalStrategy;
  }

  TableTypes getTableTypes()
  {
    return tableTypes;
  }

  TypeMap getTypeMap()
  {
    return typeMap;
  }

  boolean isSupportsCatalogs()
  {
    return supportsCatalogs;
  }

  boolean isSupportsSchemas()
  {
    return supportsSchemas;
  }

}
