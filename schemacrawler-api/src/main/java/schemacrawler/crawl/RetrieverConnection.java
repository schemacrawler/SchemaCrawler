/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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


import static java.util.Objects.requireNonNull;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.DatabaseSpecificOverrideOptions;
import schemacrawler.schemacrawler.InformationSchemaViews;
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

  private static String lookupIdentifierQuoteString(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                                                    final DatabaseMetaData metaData)
    throws SQLException
  {
    String identifierQuoteString;
    if (databaseSpecificOverrideOptions.hasOverrideForIdentifierQuoteString())
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

  private static List<String> lookupReservedWords(final DatabaseMetaData metaData)
    throws SQLException
  {
    final Set<String> rawReservedWords = new HashSet<>();
    rawReservedWords
      .addAll(Arrays.asList(metaData.getSQLKeywords().split(",")));
    rawReservedWords.addAll(Arrays.asList(Utility
      .readResourceFully("/sql2003_reserved_words.txt").split("\r\n")));
    final List<String> reservedWordsList = new ArrayList<>();
    for (final String reservedWord: rawReservedWords)
    {
      reservedWordsList.add(reservedWord.trim().toUpperCase());
    }
    Collections.sort(reservedWordsList);
    return Collections.unmodifiableList(reservedWordsList);
  }

  private static boolean lookupSupportsCatalogs(final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions,
                                                final DatabaseMetaData metaData)
    throws SQLException
  {
    final boolean supportsCatalogs;
    if (databaseSpecificOverrideOptions.hasOverrideForSupportsCatalogs())
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
    if (databaseSpecificOverrideOptions.hasOverrideForSupportsSchemas())
    {
      supportsSchemas = databaseSpecificOverrideOptions.isSupportsSchemas();
    }
    else
    {
      supportsSchemas = metaData.supportsSchemasInTableDefinitions();
    }

    return supportsSchemas;
  }

  private static final Logger LOGGER = Logger
    .getLogger(RetrieverConnection.class.getName());

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
                      final SchemaCrawlerOptions options)
    throws SQLException
  {
    SchemaCrawlerOptions schemaCrawlerOptions = options;
    if (schemaCrawlerOptions == null)
    {
      schemaCrawlerOptions = new SchemaCrawlerOptions();
    }

    requireNonNull(connection, "No connection provided");
    if (connection.isClosed())
    {
      throw new SQLException("Connection is closed");
    }
    this.connection = connection;
    metaData = connection.getMetaData();

    informationSchemaViews = schemaCrawlerOptions.getInformationSchemaViews();

    final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = schemaCrawlerOptions
      .getDatabaseSpecificOverrideOptions();

    supportsCatalogs = lookupSupportsCatalogs(databaseSpecificOverrideOptions,
                                              metaData);
    LOGGER.log(Level.CONFIG, String
      .format("Database %s catalogs", supportsCatalogs? "supports"
                                                      : "does not support"));

    supportsSchemas = lookupSupportsSchemas(databaseSpecificOverrideOptions,
                                            metaData);
    LOGGER
      .log(Level.CONFIG, String.format("Database %s schemas",
                                       supportsSchemas? "supports"
                                                      : "does not support"));

    identifierQuoteString = lookupIdentifierQuoteString(databaseSpecificOverrideOptions,
                                                        metaData);
    LOGGER.log(Level.CONFIG, String
      .format("Database identifier quote string is \"%s\"",
              identifierQuoteString));

    tableTypes = new TableTypes(connection);
    LOGGER.log(Level.CONFIG,
               String.format("Supported table types are %s", tableTypes));

    reservedWords = lookupReservedWords(metaData);

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
    if (name != null
        && identifierQuoteString != null
        && (Utility.containsWhitespace(name) || reservedWords.contains(name
          .toUpperCase())))
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
