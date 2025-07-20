/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.schema.options;

public enum HideDatabaseObjectsType {
  hideRoutines("hide_routines"),
  hideSchemas("hide_schemas"),
  hideSequences("hide_sequences"),
  hideSynonyms("hide_synonyms"),
  hideTables("hide_tables"),
  ;

  private static final String SCHEMACRAWLER_FORMAT_PREFIX = "schemacrawler.format.";

  private final String key;

  HideDatabaseObjectsType(final String key) {
    this.key = key;
  }

  String getKey() {
    return SCHEMACRAWLER_FORMAT_PREFIX + key;
  }
}
