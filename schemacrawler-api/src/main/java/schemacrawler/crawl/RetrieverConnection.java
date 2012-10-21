/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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

  private final Connection connection;
  private final DatabaseMetaData metaData;
  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;
  private final String identifierQuoteString;
  private final List<String> reservedWords;
  private final InformationSchemaViews informationSchemaViews;

  RetrieverConnection(final Connection connection,
                      final SchemaCrawlerOptions options)
    throws SQLException
  {
    SchemaCrawlerOptions schemaCrawlerOptions = options;
    if (schemaCrawlerOptions == null)
    {
      schemaCrawlerOptions = new SchemaCrawlerOptions();
    }
    if (connection == null)
    {
      throw new SQLException("No connection provided");
    }
    if (connection.isClosed())
    {
      throw new SQLException("Connection is closed");
    }
    this.connection = connection;
    metaData = connection.getMetaData();

    informationSchemaViews = schemaCrawlerOptions.getInformationSchemaViews();

    final DatabaseSpecificOverrideOptions databaseSpecificOverrideOptions = schemaCrawlerOptions
      .getDatabaseSpecificOverrideOptions();
    if (databaseSpecificOverrideOptions.hasOverrideForSupportsCatalogs())
    {
      supportsCatalogs = databaseSpecificOverrideOptions.isSupportsCatalogs();
    }
    else
    {
      supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
    }
    LOGGER.log(Level.CONFIG, String
      .format("Database %s catalogs", supportsCatalogs? "supports"
                                                      : "does not support"));

    if (databaseSpecificOverrideOptions.hasOverrideForSupportsSchemas())
    {
      supportsSchemas = databaseSpecificOverrideOptions.isSupportsSchemas();
    }
    else
    {
      supportsSchemas = metaData.supportsSchemasInTableDefinitions();
    }
    LOGGER
      .log(Level.CONFIG, String.format("Database %s schemas",
                                       supportsSchemas? "supports"
                                                      : "does not support"));

    final String identifierQuoteString;
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
      this.identifierQuoteString = "";
    }
    else
    {
      this.identifierQuoteString = identifierQuoteString;
    }
    LOGGER.log(Level.CONFIG, String
      .format("Database identifier quote string is \"%s\"",
              this.identifierQuoteString));

    final Set<String> rawReservedWords = new HashSet<String>();
    rawReservedWords
      .addAll(Arrays.asList(metaData.getSQLKeywords().split(",")));
    rawReservedWords.addAll(Arrays.asList(Utility
      .readResourceFully("/sql2003_reserved_words.txt").split("\r\n")));
    final List<String> reservedWordsList = new ArrayList<String>();
    for (final String reservedWord: rawReservedWords)
    {
      reservedWordsList.add(reservedWord.trim().toUpperCase());
    }
    Collections.sort(reservedWordsList);
    reservedWords = Collections.unmodifiableList(reservedWordsList);
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

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  List<String> getReservedWords()
  {
    return reservedWords;
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
