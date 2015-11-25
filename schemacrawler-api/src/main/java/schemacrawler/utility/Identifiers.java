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
package schemacrawler.utility;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.containsWhitespace;
import static sf.util.Utility.filterOutBlank;
import static sf.util.Utility.isBlank;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Allows working with database object identifiers. All SQL 2003
 * keywords are considered identifiers. If a live connection is
 * provided, a list of valid identifiers is obtained from the database
 * server as well. Several utility methods for looking up and quoting
 * and unquoting identifiers are provided.
 */
public final class Identifiers
{

  private static final Logger LOGGER = Logger
    .getLogger(Identifiers.class.getName());

  private static final Function<String, String> toUpperCase = word -> word
    .trim().toUpperCase();

  private static final Pattern isIdentifierPattern = Pattern
    .compile("^[\\p{Nd}\\p{L}\\p{M}_]*$");
  private static final Pattern isNumericPattern = Pattern.compile("^\\p{Nd}*$");

  /**
   * Checks if the name is valid database object identifier, according
   * to the rules of most databases.
   *
   * @param name
   *        Name to check.
   * @return Whether the name is valid database object identifier.
   */
  private static boolean isIdentifier(final String name)
  {
    if (isBlank(name))
    {
      return false;
    }
    else
    {
      return isIdentifierPattern.matcher(name).matches();
    }
  }

  /**
   * Checks if the name is composed of all numbers.
   *
   * @param name
   *        Name to check.
   * @return Whether the string consists of all numbers.
   */
  private static boolean isNumeric(final String name)
  {
    if (isBlank(name))
    {
      return false;
    }
    else
    {
      return isNumericPattern.matcher(name).matches();
    }
  }

  /**
   * Load a list of SQL 2003 reserved words, and normalize them by
   * converting to uppercase.
   */
  private static Collection<String> loadSql2003ReservedWords()
  {
    final BufferedReader reader = new BufferedReader(new InputStreamReader(Identifiers.class
      .getResourceAsStream("/sql2003_reserved_words.txt")));

    final Set<String> reservedWords = new HashSet<>();
    reader.lines().filter(filterOutBlank).map(toUpperCase)
      .forEach(reservedWord -> reservedWords.add(reservedWord));
    if (reservedWords.isEmpty())
    {
      throw new RuntimeException("No SQL 2003 reserved words found");
    }
    return reservedWords;
  }

  /**
   * Lookup a list of reserved words for a database system, using
   * database metadata.
   */
  private static Collection<String> lookupReservedWords(final DatabaseMetaData metaData)
  {
    String sqlKeywords = "";
    try
    {
      sqlKeywords = metaData.getSQLKeywords();
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not retrieve SQL keywords metadata", e);
    }

    return Stream.of(sqlKeywords.split(",")).filter(filterOutBlank)
      .map(toUpperCase).collect(Collectors.toSet());
  }

  private final String identifierQuoteString;
  private final Collection<String> reservedWords;

  /**
   * Constructs a list of database object identifiers from SQL 2003
   * keywords.
   */
  public Identifiers()
  {
    reservedWords = loadSql2003ReservedWords();

    identifierQuoteString = "";
  }

  /**
   * Constructs a list of database object identifiers from SQL 2003
   * keywords, and from the database server. Also obtains the identifier
   * quote string from the database server.
   *
   * @param connection
   *        Live database connection
   */
  public Identifiers(final Connection connection)
    throws SQLException
  {
    this(connection, null);
  }

  /**
   * Constructs a list of database object identifiers from SQL 2003
   * keywords, and from the database server. Also uses the string used
   * to quote database object identifiers as provided. If this is null,
   * it obtains the identifier quote string from the database server.
   *
   * @param connection
   *        Live database connection
   * @param identifierQuoteString
   *        Identifier quote string override, or null if not overridden
   */
  public Identifiers(final Connection connection,
                     final String identifierQuoteString)
                       throws SQLException
  {
    requireNonNull(connection, "No connection provided");
    final DatabaseMetaData metaData = requireNonNull(connection
      .getMetaData(), "No database metadata obtained");

    reservedWords = loadSql2003ReservedWords();
    reservedWords.addAll(lookupReservedWords(metaData));

    if (identifierQuoteString == null)
    {
      final String metaDataIdentifierQuoteString = metaData
        .getIdentifierQuoteString();
      if (metaDataIdentifierQuoteString == null)
      {
        this.identifierQuoteString = "";
      }
      else
      {
        this.identifierQuoteString = metaDataIdentifierQuoteString;
      }
    }
    else
    {
      this.identifierQuoteString = identifierQuoteString;
    }
  }

  /**
   * Gets the string used to quote database object identifiers, as
   * provided by the database server, or as overridden by the caller.
   *
   * @return Identifier quote string
   */
  public String getIdentifierQuoteString()
  {
    return identifierQuoteString;
  }

  /**
   * Get a list of reserved words, normalized to uppercase.
   */
  public Collection<String> getReservedWords()
  {
    return new HashSet<>(reservedWords);
  }

  /**
   * Checks if an identifier name is quoted using the identifier quote
   * character.
   * 
   * @param name
   *        Identifier name to check
   * @return Whether the identifier name is quoted
   */
  public boolean isQuotedName(final String name)
  {
    if (isBlank(name) || identifierQuoteString.isEmpty())
    {
      return false;
    }

    final int quoteLength = identifierQuoteString.length();
    return name.startsWith(identifierQuoteString)
           && name.endsWith(identifierQuoteString)
           && name.length() >= quoteLength * 2;
  }

  /**
   * Checks if a given word is a reserved word. Searches are
   * case-insensitive.
   * 
   * @param word
   *        Word to check
   * @return Whether the given word is reserved
   */
  public boolean isReservedWord(final String word)
  {
    return filterOutBlank.test(word)
           && reservedWords.contains(toUpperCase.apply(word));
  }

  /**
   * Checks if a given identifier name needs to be quoted. It uses
   * generalized rules which are common across the majority of
   * databases.
   * 
   * @param name
   *        Identifier name to check
   * @return Whether the given name needs to be quoted
   */
  public boolean isToBeQuoted(final String name)
  {
    if (name == null || name.isEmpty() || isQuotedName(name))
    {
      return false;
    }
    else
    {
      return containsWhitespace(name) || isNumeric(name)
             || containsSpecialCharacters(name) || isReservedWord(name);
    }
  }

  /**
   * Quotes an identifier name using the identifier quote string. Does
   * not quote the identifier name if quoting is not required, per
   * generalized database rules.
   * 
   * @param name
   *        Identifier name to quote
   * @return Identifier name after quoting it, or the original name if
   *         quoting is not required
   */
  public String quotedName(final String name)
  {
    final String quotedName;
    if (isToBeQuoted(name))
    {
      quotedName = identifierQuoteString + name + identifierQuoteString;
    }
    else
    {
      quotedName = name;
    }
    return quotedName;
  }

  /**
   * Remove quotes from an identifier name using the identifier quote
   * string. Returns the original name if it was not quoted.
   * 
   * @param name
   *        Identifier name to remove quotes from
   * @return Identifier name after quoting it, or the original name if
   *         quoting is not required
   */
  public String unquotedName(final String name)
  {
    if (isBlank(name))
    {
      return name;
    }

    final String unquotedName;
    final int quoteLength = identifierQuoteString.length();
    if (isQuotedName(name))
    {
      unquotedName = name.substring(quoteLength, name.length() - quoteLength);
    }
    else
    {
      unquotedName = name;
    }
    return unquotedName;
  }

  /**
   * Checks if an identifier name contains characters other than the
   * ones allowed by most databases for identifier names.
   */
  private boolean containsSpecialCharacters(final String name)
  {
    return !isIdentifier(name);
  }

}
