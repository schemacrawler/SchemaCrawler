/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.loader.attributes.model;

import static java.util.Objects.requireNonNull;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

public class WeakAssociationAttributes extends ObjectAttributes {

  private static final long serialVersionUID = 8305929253225133307L;

  private final TableAttributes dependentTable;
  private final TableAttributes referencedTable;
  private final Map<String, String> columnReferences;

  @ConstructorProperties({
    "name",
    "remarks",
    "attributes",
    "referenced-table",
    "referencing-table",
    "column-references"
  })
  public WeakAssociationAttributes(
      final String name,
      final List<String> remarks,
      final Map<String, String> attributes,
      final TableAttributes referencedTable,
      final TableAttributes dependentTable,
      final Map<String, String> columnReferences) {
    super(name, remarks, attributes);
    this.referencedTable = requireNonNull(referencedTable, "No referenced table provided");
    this.dependentTable = requireNonNull(dependentTable, "No referencing table provided");
    if (columnReferences == null || columnReferences.isEmpty()) {
      throw new IllegalArgumentException("No column references provided");
    }
    this.columnReferences = columnReferences;
  }

  public Map<String, String> getColumnReferences() {
    return columnReferences;
  }

  public TableAttributes getReferencedTable() {
    return referencedTable;
  }

  public TableAttributes getDependentTable() {
    return dependentTable;
  }
}
