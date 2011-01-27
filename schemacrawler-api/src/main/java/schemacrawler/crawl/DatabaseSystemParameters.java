/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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

import schemacrawler.schemacrawler.Options;
import sf.util.Utility;

final class DatabaseSystemParameters
  implements Options
{

  private static final long serialVersionUID = -4734554820673397484L;

  private static final Logger LOGGER = Logger
    .getLogger(DatabaseSystemParameters.class.getName());

  private final boolean supportsCatalogs;
  private final boolean supportsSchemas;
  private final String identifierQuoteString;
  private final List<String> reservedWords;

  DatabaseSystemParameters(final Connection connection)
    throws SQLException
  {
    final DatabaseMetaData dbMetaData = connection.getMetaData();

    supportsCatalogs = dbMetaData.supportsCatalogsInTableDefinitions();
    LOGGER.log(Level.CONFIG, String
      .format("Database %s catalogs", (supportsCatalogs? "supports"
                                                       : "does not support")));

    supportsSchemas = dbMetaData.supportsSchemasInTableDefinitions();
    LOGGER.log(Level.CONFIG, String
      .format("Database %s schemas", (supportsSchemas? "supports"
                                                     : "does not support")));

    identifierQuoteString = dbMetaData.getIdentifierQuoteString();

    final Set<String> rawReservedWords = new HashSet<String>();
    rawReservedWords.addAll(Arrays.asList(dbMetaData.getSQLKeywords()
      .split(",")));
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

  String getIdentifierQuoteString()
  {
    return identifierQuoteString;
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
