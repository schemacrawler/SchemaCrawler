/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static schemacrawler.schemacrawler.InformationSchemaKeyType.ADDITIONAL_INFO;
import static schemacrawler.schemacrawler.InformationSchemaKeyType.DATABASE_METADATA;
import static schemacrawler.schemacrawler.InformationSchemaKeyType.INFORMATION_SCHEMA;
import static schemacrawler.schemacrawler.InformationSchemaKeyType.METADATA_EXTENSION;
import static schemacrawler.schemacrawler.InformationSchemaKeyType.OPERATION;

public enum InformationSchemaKey {
  ADDITIONAL_COLUMN_ATTRIBUTES(ADDITIONAL_INFO),
  ADDITIONAL_TABLE_ATTRIBUTES(ADDITIONAL_INFO),
  CHECK_CONSTRAINTS(INFORMATION_SCHEMA),
  CONSTRAINT_COLUMN_USAGE(INFORMATION_SCHEMA),
  DATABASE_USERS(ADDITIONAL_INFO),
  EXT_HIDDEN_TABLE_COLUMNS(METADATA_EXTENSION),
  EXT_INDEXES(METADATA_EXTENSION),
  EXT_SYNONYMS(METADATA_EXTENSION),
  EXT_TABLES(METADATA_EXTENSION),
  EXT_TABLE_CONSTRAINTS(METADATA_EXTENSION),
  FOREIGN_KEYS(DATABASE_METADATA),
  FUNCTIONS(DATABASE_METADATA),
  FUNCTION_COLUMNS(DATABASE_METADATA),
  INDEXES(DATABASE_METADATA),
  PRIMARY_KEYS(DATABASE_METADATA),
  PROCEDURES(DATABASE_METADATA),
  PROCEDURE_COLUMNS(DATABASE_METADATA),
  ROUTINES(INFORMATION_SCHEMA),
  SCHEMATA(INFORMATION_SCHEMA),
  SEQUENCES(INFORMATION_SCHEMA),
  SERVER_INFORMATION(ADDITIONAL_INFO),
  TABLES(DATABASE_METADATA),
  TABLE_COLUMNS(DATABASE_METADATA),
  TABLE_COLUMN_PRIVILEGES(DATABASE_METADATA),
  TABLE_CONSTRAINTS(INFORMATION_SCHEMA),
  TABLE_PRIVILEGES(DATABASE_METADATA),
  TABLESAMPLE(OPERATION), // TABLESAMPLE (no underscore) is an operation
  TRIGGERS(INFORMATION_SCHEMA),
  TYPE_INFO(DATABASE_METADATA),
  VIEWS(INFORMATION_SCHEMA),
  VIEW_TABLE_USAGE(INFORMATION_SCHEMA),
  ;

  private final InformationSchemaKeyType type;

  InformationSchemaKey(final InformationSchemaKeyType type) {
    this.type = type;
  }

  public String description() {
    return type + "." + name();
  }

  /**
   * @return the type
   */
  public InformationSchemaKeyType getType() {
    return type;
  }
}
