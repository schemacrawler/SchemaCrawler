/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.CollectionsUtility.splitList;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import us.fatehi.utility.ioresource.ClasspathInputResource;

public class IdentifiersBuilder implements OptionsBuilder<IdentifiersBuilder, Identifiers> {

  private static final Logger LOGGER = Logger.getLogger(Identifiers.class.getName());

  public static IdentifiersBuilder builder() {
    return new IdentifiersBuilder();
  }

  /** Load a list of SQL 2003 reserved words, and normalize them by converting to uppercase. */
  private static Collection<String> loadSql2003ReservedWords() {
    final Set<String> reservedWords = new HashSet<>();
    try (final BufferedReader reader =
        new ClasspathInputResource("/sql2003_reserved_words.txt").openNewInputReader(UTF_8)) {
      String line;
      while ((line = reader.readLine()) != null) {
        reservedWords.add(line);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not read list of SQL 2003 reserved words", e);
    }
    if (reservedWords.isEmpty()) {
      throw new InternalRuntimeException("No SQL 2003 reserved words found");
    }

    return toUpperCase(reservedWords);
  }

  /** Lookup a list of reserved words for a database system, using database metadata. */
  private static Collection<String> lookupReservedWords(final DatabaseMetaData metaData) {
    String sqlKeywords = "";
    try {
      sqlKeywords = metaData.getSQLKeywords();
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not retrieve SQL keywords metadata", e);
    }
    if (isBlank(sqlKeywords)) {
      return emptyList();
    }

    return toUpperCase(asList(splitList(sqlKeywords)));
  }

  /**
   * Remove mixed case duplicates and blanks, and return a light-weight serializable collection of
   * upper-case words.
   *
   * @param words Collection of words
   * @return Serializable collection of unique upper-case words.
   */
  private static Collection<String> toUpperCase(final Iterable<String> words) {
    final Collection<String> upperCaseWords = new HashSet<>();
    if (words != null) {
      for (final String word : words) {
        if (!isBlank(word)) {
          upperCaseWords.add(trimToEmpty(word.toUpperCase()));
        }
      }
    }
    // Create a lighter-weight data structure that is serializable
    return new ArrayList<>(upperCaseWords);
  }

  final Collection<String> reservedWords;
  String identifierQuoteString;
  boolean quoteMixedCaseIdentifiers;

  IdentifierQuotingStrategy identifierQuotingStrategy;

  private IdentifiersBuilder() {
    reservedWords = loadSql2003ReservedWords();
    identifierQuotingStrategy =
        IdentifierQuotingStrategy.quote_if_special_characters_and_reserved_words;
    quoteMixedCaseIdentifiers = true;
  }

  public IdentifiersBuilder doNotQuoteMixedCaseIdentifiers() {
    quoteMixedCaseIdentifiers = false;
    return this;
  }

  /**
   * Constructs a list of database object identifiers from SQL 2003 keywords, and from the database
   * server. Also obtains the identifier quote string from the database server.
   *
   * @param connection Live database connection
   * @return Builder
   * @throws SQLException
   */
  public IdentifiersBuilder fromConnection(final Connection connection) {
    if (connection == null) {
      return this;
    }
    try {
      final DatabaseMetaData metaData =
          requireNonNull(connection.getMetaData(), "No database metadata obtained");

      reservedWords.addAll(lookupReservedWords(metaData));

      if (!isIdentifierQuoteStringSet()) {
        final String metaDataIdentifierQuoteString = metaData.getIdentifierQuoteString();
        if (metaDataIdentifierQuoteString != null) {
          identifierQuoteString = metaDataIdentifierQuoteString;
        }
      }

      quoteMixedCaseIdentifiers = !metaData.supportsMixedCaseIdentifiers();

    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, "Could not obtain connection-specific information", e);
    }
    return this;
  }

  @Override
  public IdentifiersBuilder fromOptions(final Identifiers identifiers) {
    if (identifiers == null) {
      return this;
    }

    identifierQuoteString = identifiers.getIdentifierQuoteString();
    identifierQuotingStrategy = identifiers.getIdentifierQuotingStrategy();
    quoteMixedCaseIdentifiers = identifiers.isQuoteMixedCaseIdentifiers();

    return this;
  }

  public boolean isIdentifierQuoteStringSet() {
    return identifierQuoteString != null;
  }

  public IdentifiersBuilder quoteMixedCaseIdentifiers() {
    quoteMixedCaseIdentifiers = true;
    return this;
  }

  @Override
  public Identifiers toOptions() {
    return new Identifiers(this);
  }

  /**
   * Uses the string used to quote database object identifiers as provided.
   *
   * @param identifierQuoteString Identifier quote string override, or null if not overridden
   * @return Builder
   */
  public IdentifiersBuilder withIdentifierQuoteString(final String identifierQuoteString) {
    if (isBlank(identifierQuoteString)) {
      // The JDBC specification states that spaces are to be treated as
      // if identifier quoting is not supported.
      this.identifierQuoteString = "";
    } else {
      this.identifierQuoteString = identifierQuoteString;
    }
    return this;
  }

  /**
   * Specifies how to quote database object identifiers.
   *
   * @param identifierQuotingStrategy Set identifier quoting strategy, or turn off quoting if null
   * @return Builder
   */
  public IdentifiersBuilder withIdentifierQuotingStrategy(
      final IdentifierQuotingStrategy identifierQuotingStrategy) {
    if (identifierQuotingStrategy == null) {
      this.identifierQuotingStrategy = IdentifierQuotingStrategy.quote_none;
    } else {
      this.identifierQuotingStrategy = identifierQuotingStrategy;
    }
    return this;
  }
}
