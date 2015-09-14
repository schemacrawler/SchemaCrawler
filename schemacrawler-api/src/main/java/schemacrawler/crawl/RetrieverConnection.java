/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.utility.JavaSqlTypes;
import schemacrawler.utility.TypeMap;
import sf.util.Utility;

/**
 * A connection for the retriever. Wraps a live database connection.
 *
 * @author Sualeh Fatehi
 */
final class RetrieverConnection
{

  private static final Logger LOGGER = Logger
    .getLogger(RetrieverConnection.class.getName());

  private static String
    lookupIdentifierQuoteString(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
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
    if (Utility.isBlank(identifierQuoteString))
    {
      identifierQuoteString = "";
    }

    return identifierQuoteString;
  }

  private static List<String>
    lookupReservedWords(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                        final DatabaseMetaData metaData)
                          throws SQLException
  {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(RetrieverConnection.class
      .getResourceAsStream("/sql2003_reserved_words.txt")));

    if (databaseSpecificOverrideOptions != null
        && databaseSpecificOverrideOptions.isSupportsReservedWords())
    {
      return Collections.unmodifiableList(reader.lines()
        .map(reservedWord -> reservedWord.trim().toUpperCase()).distinct()
        .sorted().collect(Collectors.toList()));
    }
    else
    {
      return Collections.unmodifiableList(Stream
        .concat(Stream.of(metaData.getSQLKeywords().split(",")), reader.lines())
        .map(reservedWord -> reservedWord.trim().toUpperCase()).distinct()
        .sorted().collect(Collectors.toList()));
    }
  }

  private static boolean lookupSupportsCatalogs(
                                                final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
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

  private static boolean lookupSupportsSchemas(
                                               final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
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
  private final String identifierQuoteString;
  private final List<String> reservedWords;
  private final InformationSchemaViews informationSchemaViews;
  private final TableTypes tableTypes;
  private final JavaSqlTypes javaSqlTypes;
  private final TypeMap typeMap;

  RetrieverConnection(final Connection connection,
                      final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                      final SchemaCrawlerOptions options)
                        throws SQLException
  {
    SchemaCrawlerOptions schemaCrawlerOptions = options;
    if (schemaCrawlerOptions == null)
    {
      schemaCrawlerOptions = new SchemaCrawlerOptions();
    }

    try
    {
      checkConnection(connection);
    }
    catch (SchemaCrawlerException e)
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
    LOGGER.log(Level.CONFIG,
               String.format("Database %s catalogs",
                             supportsCatalogs? "supports": "does not support"));

    supportsSchemas = lookupSupportsSchemas(databaseSpecificOverrideOptions,
                                            metaData);
    LOGGER.log(Level.CONFIG,
               String.format("Database %s schemas",
                             supportsSchemas? "supports": "does not support"));

    identifierQuoteString = lookupIdentifierQuoteString(databaseSpecificOverrideOptions,
                                                        metaData);
    LOGGER.log(Level.CONFIG,
               String.format("Database identifier quote string is \"%s\"",
                             identifierQuoteString));

    tableTypes = new TableTypes(connection);
    LOGGER.log(Level.CONFIG,
               String.format("Supported table types are %s", tableTypes));

    reservedWords = lookupReservedWords(databaseSpecificOverrideOptions,
                                        metaData);

    typeMap = new TypeMap(connection);
    javaSqlTypes = new JavaSqlTypes();
  }

  Connection getConnection()
  {
    return connection;
  }

  String getIdentifierQuoteString()
  {
    return identifierQuoteString;
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

  List<String> getReservedWords()
  {
    return reservedWords;
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

  boolean needsToBeQuoted(final String name)
  {
    final boolean needsToBeQuoted;
    if (name != null && identifierQuoteString != null
        && (Utility.containsWhitespace(name)
            || reservedWords.contains(name.toUpperCase())))
    {
      needsToBeQuoted = true;
    }
    else
    {
      needsToBeQuoted = false;
    }
    return needsToBeQuoted;
  }

}
