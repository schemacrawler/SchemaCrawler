/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schemacrawler;

public enum IdentifierQuotingStrategy {
  quote_none,
  quote_all,
  quote_if_special_characters,
  quote_if_special_characters_and_reserved_words,
}
