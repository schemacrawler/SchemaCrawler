/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.schema.options;

public enum HideDatabaseObjectNamesType {
  hideAlternateKeyNames("hide_alternatekey_names"),
  hideForeignKeyNames("hide_foreignkey_names"),
  hideIndexNames("hide_index_names"),
  hidePrimaryKeyNames("hide_primarykey_names"),
  hideRoutineSpecificNames("hide_routine_specific_names"),
  hideTableConstraintNames("hide_constraint_names"),
  hideTriggerNames("hide_trigger_names"),
  hideWeakAssociationNames("hide_weakassociation_names"),
  ;

  private static final String SCHEMACRAWLER_FORMAT_PREFIX = "schemacrawler.format.";

  private final String key;

  HideDatabaseObjectNamesType(final String key) {
    this.key = key;
  }

  String getKey() {
    return SCHEMACRAWLER_FORMAT_PREFIX + key;
  }
}
