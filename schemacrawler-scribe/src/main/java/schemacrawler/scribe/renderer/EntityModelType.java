/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.renderer;

import schemacrawler.ermodel.model.EntityType;
import schemacrawler.ermodel.utility.ERModelUtility;
import schemacrawler.schema.Table;

public enum EntityModelType {
  unknown,
  non_entity,
  subtype,
  weak_entity,
  strong_entity,
  bridge_table,
  ;

  public static EntityModelType from(final Table table) {
    if (table == null) {
      return EntityModelType.unknown;
    }
    if (ERModelUtility.inferBridgeTable(table).toBoolean(false)) {
      return EntityModelType.bridge_table;
    }
    final EntityType entityType = ERModelUtility.inferEntityType(table);
    return switch (entityType) {
      case strong_entity -> EntityModelType.strong_entity;
      case subtype -> EntityModelType.subtype;
      case weak_entity -> EntityModelType.weak_entity;
      case non_entity -> EntityModelType.non_entity;
      default -> EntityModelType.unknown;
    };
  }
}
