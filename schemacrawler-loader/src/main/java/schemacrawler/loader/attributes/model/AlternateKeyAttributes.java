/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.attributes.model;

import static us.fatehi.utility.Utility.isBlank;

import java.beans.ConstructorProperties;
import java.io.Serial;
import java.util.List;
import java.util.Map;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaReference;

public class AlternateKeyAttributes extends ObjectAttributes {

  @Serial private static final long serialVersionUID = -3510286847668145323L;

  private final String schemaName;
  private final String catalogName;
  private final String tableName;
  private final List<String> columns;

  @ConstructorProperties({"schema", "catalog", "table", "name", "remarks", "attributes", "columns"})
  public AlternateKeyAttributes(
      final String schemaName,
      final String catalogName,
      final String tableName,
      final String name,
      final List<String> remarks,
      final Map<String, String> attributes,
      final List<String> columns) {
    super(name, remarks, attributes);
    this.catalogName = catalogName;
    this.schemaName = schemaName;
    if (isBlank(tableName)) {
      throw new IllegalArgumentException("No table name provided");
    }
    this.tableName = tableName;
    if (columns == null || columns.isEmpty()) {
      throw new IllegalArgumentException("No columns provided");
    }
    this.columns = List.copyOf(columns);
  }

  public List<String> getColumns() {
    return columns;
  }

  public Schema getSchema() {
    return new SchemaReference(catalogName, schemaName);
  }

  public String getTableName() {
    return tableName;
  }

  @Override
  public String toString() {
    return "Alternate key attributes <%s.%s.%s.%s[%s]>"
        .formatted(schemaName, catalogName, tableName, getName(), columns);
  }
}
