/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DependantObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;

/**
 * Allows working with database object identifiers. All SQL 2003 keywords are considered
 * identifiers. If a live connection is provided, a list of valid identifiers is obtained from the
 * database server as well. Several utility methods for looking up and quoting and unquoting
 * identifiers are provided.
 */
public final class Identifiers {

  public static class Builder {

    /** Load a list of SQL 2003 reserved words, and normalize them by converting to uppercase. */
    private static Collection<String> loadSql2003ReservedWords() {
      final Set<String> reservedWords = new HashSet<>();
      try (final BufferedReader reader =
          new BufferedReader(
              new InputStreamReader(
                  Identifiers.class.getResourceAsStream("/sql2003_reserved_words.txt")))) {
        String line;
        while ((line = reader.readLine()) != null) {
          if (!isBlank(line)) {
            reservedWords.add(line);
          }
        }
      } catch (final IOException e) {
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

      return toUpperCase(Arrays.asList(sqlKeywords.split(",")));
    }

    private static Collection<String> toUpperCase(final Iterable<String> words) {
      final Collection<String> upperCaseWords = new HashSet<>();
      if (words != null) {
        for (final String word : words) {
          if (!isBlank(word)) {
            upperCaseWords.add(word.toUpperCase());
          }
        }
      }
      return upperCaseWords;
    }

    private final Collection<String> reservedWords;
    private String identifierQuoteString;
    private IdentifierQuotingStrategy identifierQuotingStrategy;

    private Builder() {
      reservedWords = loadSql2003ReservedWords();
      identifierQuotingStrategy =
          IdentifierQuotingStrategy.quote_if_special_characters_and_reserved_words;
    }

    public Identifiers build() {
      return new Identifiers(this);
    }

    /**
     * Constructs a list of database object identifiers from SQL 2003 keywords, and from the
     * database server. Also obtains the identifier quote string from the database server.
     *
     * @param connection Live database connection
     * @return Builder
     * @throws SQLException
     */
    public Builder withConnection(final Connection connection) throws SQLException {
      requireNonNull(connection, "No connection provided");
      final DatabaseMetaData metaData =
          requireNonNull(connection.getMetaData(), "No database metadata obtained");

      reservedWords.addAll(lookupReservedWords(metaData));

      if (!isIdentifierQuoteStringSet()) {
        final String metaDataIdentifierQuoteString = metaData.getIdentifierQuoteString();
        if (metaDataIdentifierQuoteString != null) {
          identifierQuoteString = metaDataIdentifierQuoteString;
        }
      }

      return this;
    }

    /**
     * Tries to use the connection, but does not throw any exceptions.
     *
     * @param connection Live database connection
     * @return Builder
     * @see #withConnection(Connection connection)
     */
    public Builder withConnectionIfPossible(final Connection connection) {
      try {
        withConnection(connection);
      } catch (NullPointerException | SQLException e) {
        // Ignore
      }
      return this;
    }

    /**
     * Uses the string used to quote database object identifiers as provided.
     *
     * @param identifierQuoteString Identifier quote string override, or null if not overridden
     * @return Builder
     */
    public Builder withIdentifierQuoteString(final String identifierQuoteString) {
      if (isBlank(identifierQuoteString)) {
        // The JDBC specification states that spaces are to treated as
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
    public Builder withIdentifierQuotingStrategy(
        final IdentifierQuotingStrategy identifierQuotingStrategy) {
      if (identifierQuotingStrategy == null) {
        this.identifierQuotingStrategy = IdentifierQuotingStrategy.quote_none;
      } else {
        this.identifierQuotingStrategy = identifierQuotingStrategy;
      }
      return this;
    }

    private boolean isIdentifierQuoteStringSet() {
      return identifierQuoteString != null;
    }
  }

  private static final Logger LOGGER = Logger.getLogger(Identifiers.class.getName());

  public static final Identifiers STANDARD =
      Identifiers.identifiers().withIdentifierQuoteString("\"").build();

  private static final Pattern isAllNumeric = Pattern.compile("^\\p{Nd}*$");
  private static final Pattern isIdentifier = Pattern.compile("^[\\p{Nd}\\p{L}\\p{M}_]*$");

  public static Builder identifiers() {
    return new Builder();
  }

  /**
   * Checks if the name is valid database object identifier, according to the rules of most
   * databases.
   *
   * @param name Name to check.
   * @return Whether the name is valid database object identifier.
   */
  private static boolean isIdentifier(final String name) {
    if (isBlank(name)) {
      return false;
    } else {
      return isIdentifier.matcher(name).matches() && !isAllNumeric.matcher(name).matches();
    }
  }

  private final String identifierQuoteString;
  private final IdentifierQuotingStrategy identifierQuotingStrategy;
  private final Collection<String> reservedWords;

  private Identifiers(final Builder builder) {
    if (builder.isIdentifierQuoteStringSet()) {
      identifierQuoteString = builder.identifierQuoteString;
    } else {
      // SQL standard and JDBC default is double quotes
      identifierQuoteString = "\"";
    }
    identifierQuotingStrategy = builder.identifierQuotingStrategy;
    reservedWords = builder.reservedWords;
  }

  /**
   * Gets the string used to quote database object identifiers, as provided by the database server,
   * or as overridden by the caller.
   *
   * @return Identifier quote string
   */
  public String getIdentifierQuoteString() {
    return identifierQuoteString;
  }

  public IdentifierQuotingStrategy getIdentifierQuotingStrategy() {
    return identifierQuotingStrategy;
  }

  /**
   * Get a list of reserved words, normalized to uppercase.
   *
   * @return Reserved words
   */
  public Collection<String> getReservedWords() {
    return new HashSet<>(reservedWords);
  }

  /**
   * Checks if an identifier name is quoted using the identifier quote character.
   *
   * @param name Identifier name to check
   * @return Whether the identifier name is quoted
   */
  public boolean isQuotedName(final String name) {
    if (isBlank(name)
        || identifierQuoteString.isEmpty()
        || identifierQuotingStrategy == IdentifierQuotingStrategy.quote_none) {
      return false;
    }

    final int quoteLength = identifierQuoteString.length();
    return name.startsWith(identifierQuoteString)
        && name.endsWith(identifierQuoteString)
        && name.length() >= quoteLength * 2;
  }

  /**
   * Checks if a given word is a reserved word. Searches are case-insensitive.
   *
   * @param word Word to check
   * @return Whether the given word is reserved
   */
  public boolean isReservedWord(final String word) {
    return !isBlank(word) && reservedWords.contains(word.trim().toUpperCase());
  }

  /**
   * Checks if a given identifier name needs to be quoted. It uses generalized rules which are
   * common across the majority of databases.
   *
   * @param name Identifier name to check
   * @return Whether the given name needs to be quoted
   */
  public boolean isToBeQuoted(final String name) {
    if (name == null || name.isEmpty() || isQuotedName(name)) {
      return false;
    }

    switch (identifierQuotingStrategy) {
      case quote_none:
        return false;
      case quote_all:
        return true;
      case quote_if_special_characters:
        return !isIdentifier(name);
      case quote_if_special_characters_and_reserved_words:
      default:
        return !isIdentifier(name) || isReservedWord(name);
    }
  }

  public String quoteFullName(final DatabaseObject databaseObject) {
    final StringBuilder buffer = new StringBuilder(512);
    quoteFullName(buffer, databaseObject);
    return buffer.toString();
  }

  public String quoteFullName(final DatabaseObject parent, final String name) {
    final StringBuilder buffer = new StringBuilder(512);
    quoteFullName(buffer, parent, name);
    return buffer.toString();
  }

  public <P extends DatabaseObject> String quoteFullName(final DependantObject<P> dependantObject) {
    if (dependantObject == null) {
      return "";
    }
    return quoteFullName(dependantObject.getParent(), dependantObject.getName());
  }

  public String quoteFullName(final Schema schema) {
    final StringBuilder buffer = new StringBuilder(512);
    quoteFullName(buffer, schema);
    return buffer.toString();
  }

  /**
   * Quotes an identifier name using the identifier quote string. Does not quote the identifier name
   * if quoting is not required, per generalized database rules.
   *
   * @param namedObject Object name to quote
   * @return Identifier name after quoting it, or the original name if quoting is not required
   */
  public String quoteName(final NamedObject namedObject) {
    if (namedObject == null) {
      return "";
    }

    final StringBuilder buffer = new StringBuilder(512);
    quoteName(buffer, namedObject.getName());
    return buffer.toString();
  }

  /**
   * Quotes an identifier name using the identifier quote string. Does not quote the identifier name
   * if quoting is not required, per generalized database rules.
   *
   * @param name Identifier name to quote
   * @return Identifier name after quoting it, or the original name if quoting is not required
   */
  public String quoteName(final String name) {
    final StringBuilder buffer = new StringBuilder(512);
    quoteName(buffer, name);
    return buffer.toString();
  }

  public <P extends DatabaseObject> String quoteShortName(
      final DependantObject<P> dependantObject) {
    if (dependantObject == null) {
      return "";
    }
    final P parent = dependantObject.getParent();
    final StringBuilder buffer = new StringBuilder(64);
    if (parent != null) {
      final String parentName = parent.getName();
      if (!isBlank(parentName)) {
        quoteName(buffer, parentName);
        buffer.append('.');
      }
    }
    quoteName(buffer, dependantObject.getName());

    return buffer.toString();
  }

  /**
   * Unquotes an identifier name using the identifier quote string.
   *
   * @param name Identifier name to unquote
   * @return Identifier name after unquoting it, or the original name if quoting is not required
   */
  public String unquoteName(final String name) {
    if (isQuotedName(name)) {
      final int quoteLength = identifierQuoteString.length();
      return name.substring(quoteLength, name.length() - quoteLength);
    } else {
      return name;
    }
  }

  private void quoteFullName(final StringBuilder buffer, final DatabaseObject databaseObject) {
    requireNonNull(buffer, "No buffer provided");
    if (databaseObject == null) {
      return;
    }

    final Schema schema = databaseObject.getSchema();
    final String name = databaseObject.getName();
    quoteFullName(buffer, schema);
    if (!isBlank(name)) {
      if (buffer.length() > 0) {
        buffer.append('.');
      }
      quoteName(buffer, name);
    }
  }

  private void quoteFullName(
      final StringBuilder buffer, final DatabaseObject parent, final String name) {
    requireNonNull(buffer, "No buffer provided");
    quoteFullName(buffer, parent);
    if (!isBlank(name)) {
      if (buffer.length() > 0) {
        buffer.append('.');
      }
      quoteName(buffer, name);
    }
  }

  private void quoteFullName(final StringBuilder buffer, final Schema schema) {
    requireNonNull(buffer, "No buffer provided");
    if (schema == null) {
      return;
    }

    final String catalogName = schema.getCatalogName();
    final String schemaName = schema.getName();

    final boolean hasCatalogName = !isBlank(catalogName);
    final boolean hasSchemaName = !isBlank(schemaName);

    if (hasCatalogName) {
      quoteName(buffer, catalogName);
    }
    if (hasCatalogName && hasSchemaName) {
      buffer.append(".");
    }
    if (hasSchemaName) {
      quoteName(buffer, schemaName);
    }
  }

  private void quoteName(final StringBuilder buffer, final String name) {
    requireNonNull(buffer, "No buffer provided");
    if (isBlank(name)) {
      return;
    }

    if (isToBeQuoted(name)) {
      buffer.append(identifierQuoteString).append(name).append(identifierQuoteString);
    } else {
      buffer.append(name);
    }
  }
}
