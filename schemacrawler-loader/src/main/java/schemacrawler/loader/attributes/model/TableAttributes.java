/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.attributes.model;

import static java.util.Collections.emptyList;

import java.beans.ConstructorProperties;
import java.io.Serial;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaReference;

public class TableAttributes extends ObjectAttributes implements Iterable<ColumnAttributes> {

  @Serial private static final long serialVersionUID = -3510286847668145323L;

  private final String schemaName;
  private final String catalogName;
  private final List<ColumnAttributes> columns;

  @ConstructorProperties({"schema", "catalog", "name", "remarks", "attributes", "columns"})
  public TableAttributes(
      final String schemaName,
      final String catalogName,
      final String name,
      final List<String> remarks,
      final Map<String, String> attributes,
      final List<ColumnAttributes> columns) {
    super(name, remarks, attributes);
    this.catalogName = catalogName;
    this.schemaName = schemaName;
    if (columns == null) {
      this.columns = emptyList();
    } else {
      this.columns = List.copyOf(columns);
    }
  }

  public Schema getSchema() {
    return new SchemaReference(catalogName, schemaName);
  }

  @Override
  public Iterator<ColumnAttributes> iterator() {
    return columns.iterator();
  }

  @Override
  public String toString() {
    return "Table attributes <%s.%s.%s>".formatted(schemaName, catalogName, getName());
  }
}
