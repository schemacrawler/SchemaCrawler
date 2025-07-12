/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.schema.options;

public enum HideDependantDatabaseObjectsType {
  hideAlternateKeys("hide_alternatekeys"),
  hideForeignKeys("hide_foreignkeys"),
  hideIndexes("hide_indexes"),
  hidePrimaryKeys("hide_primarykeys"),
  hideRoutineParameters("hide_routine_parameters"),
  hideTableColumns("hide_table_columns"),
  hideTableConstraints("hide_constraints"),
  hideTriggers("hide_triggers"),
  hideWeakAssociations("hide_weakassociations"),
  ;

  private static final String SCHEMACRAWLER_FORMAT_PREFIX = "schemacrawler.format.";

  private final String key;

  HideDependantDatabaseObjectsType(final String key) {
    this.key = key;
  }

  String getKey() {
    return SCHEMACRAWLER_FORMAT_PREFIX + key;
  }
}
