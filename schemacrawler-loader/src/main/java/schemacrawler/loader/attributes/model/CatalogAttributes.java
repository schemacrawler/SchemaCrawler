/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.attributes.model;

import static java.util.Collections.emptyList;

import java.beans.ConstructorProperties;
import java.io.Serial;
import java.util.List;
import java.util.Map;

public final class CatalogAttributes extends ObjectAttributes {

  @Serial private static final long serialVersionUID = 1436642683972751860L;

  private final List<TableAttributes> tables;
  private final List<WeakAssociationAttributes> weakAssociations;
  private final List<AlternateKeyAttributes> alternateKeys;

  @ConstructorProperties({
    "name",
    "remarks",
    "attributes",
    "tables",
    "weak-associations",
    "alternate-keys"
  })
  public CatalogAttributes(
      final String name,
      final List<String> remarks,
      final Map<String, String> attributes,
      final List<TableAttributes> tables,
      final List<WeakAssociationAttributes> weakAssociations,
      final List<AlternateKeyAttributes> alternateKeys) {
    super(name, remarks, attributes);
    if (tables == null) {
      this.tables = emptyList();
    } else {
      this.tables = List.copyOf(tables);
    }
    if (weakAssociations == null) {
      this.weakAssociations = emptyList();
    } else {
      this.weakAssociations = List.copyOf(weakAssociations);
    }
    if (alternateKeys == null) {
      this.alternateKeys = emptyList();
    } else {
      this.alternateKeys = List.copyOf(alternateKeys);
    }
  }

  public List<AlternateKeyAttributes> getAlternateKeys() {
    return alternateKeys;
  }

  public List<TableAttributes> getTables() {
    return tables;
  }

  public List<WeakAssociationAttributes> getWeakAssociations() {
    return weakAssociations;
  }
}
