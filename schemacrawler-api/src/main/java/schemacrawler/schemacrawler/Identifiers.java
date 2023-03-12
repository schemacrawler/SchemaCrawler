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
package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DependantObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;

/**
 * Allows working with database object identifiers. All SQL 2003 keywords are considered
 * identifiers. If a live connection is provided, a list of valid identifiers is obtained from the
 * database server as well. Several utility methods for looking up and quoting and unquoting
 * identifiers are provided.
 */
public final class Identifiers implements Serializable {

  private static final long serialVersionUID = -5108721215361312979L;

  public static final Identifiers STANDARD =
      Identifiers.identifiers().withIdentifierQuoteString("\"").build();

  private static final Pattern isAllNumeric = Pattern.compile("^\\p{Nd}*$");
  private static final Pattern isIdentifier = Pattern.compile("^[\\p{Nd}\\p{L}\\p{M}_]*$");

  public static IdentifiersBuilder identifiers() {
    return new IdentifiersBuilder();
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
  private final boolean quoteMixedCaseIdentifiers;
  private final Collection<String> reservedWords;

  Identifiers(final IdentifiersBuilder builder) {
    if (builder.isIdentifierQuoteStringSet()) {
      identifierQuoteString = builder.identifierQuoteString;
    } else {
      // SQL standard and JDBC default is double quotes
      identifierQuoteString = "\"";
    }
    identifierQuotingStrategy = builder.identifierQuotingStrategy;
    quoteMixedCaseIdentifiers = builder.quoteMixedCaseIdentifiers;
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

  public boolean isQuoteMixedCaseIdentifiers() {
    return quoteMixedCaseIdentifiers;
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
        return !isIdentifier(name) || isMixedCase(name);
      case quote_if_special_characters_and_reserved_words:
      default:
        return !isIdentifier(name) || isMixedCase(name) || isReservedWord(name);
    }
  }

  public String quoteFullName(final DatabaseObject databaseObject) {
    final StringBuilder buffer = new StringBuilder(512);
    quoteFullName(buffer, databaseObject);
    return buffer.toString();
  }

  public <P extends DatabaseObject> String quoteFullName(final DependantObject<P> dependantObject) {
    if (dependantObject == null) {
      return "";
    }
    final StringBuilder buffer = new StringBuilder(512);
    quoteFullName(buffer, dependantObject.getParent(), dependantObject.getName());
    return buffer.toString();
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

  private boolean isMixedCase(final String name) {

    if (!quoteMixedCaseIdentifiers) {
      return false;
    }
    if (isBlank(name)) {
      return false;
    }

    boolean hasUpperCase = false;
    boolean hasLowerCase = false;

    for (int i = 0; i < name.codePointCount(0, name.length()); i++) {
      final int c = name.codePointAt(i);

      if (Character.isUpperCase(c)) {
        hasUpperCase = true;
      } else if (Character.isLowerCase(c)) {
        hasLowerCase = true;
      }

      if (hasUpperCase && hasLowerCase) {
        return true;
      }
    }

    return false;
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
