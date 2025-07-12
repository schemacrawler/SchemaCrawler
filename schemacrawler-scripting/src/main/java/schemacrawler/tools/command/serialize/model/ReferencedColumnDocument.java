/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.serialize.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schemaName", "tableName", "columnName"})
public final class ReferencedColumnDocument implements Serializable {

  private static final long serialVersionUID = -2159895984317222363L;

  private final String schemaName;
  private final String tableName;
  private final String columnName;

  public ReferencedColumnDocument(final Column column) {
    requireNonNull(column, "No column provided");

    final Table table = column.getParent();

    final String schema = table.getSchema().getFullName();
    if (!isBlank(schema)) {
      schemaName = schema;
    } else {
      schemaName = null;
    }
    tableName = table.getName();
    columnName = column.getName();
  }

  @JsonProperty("column")
  public String getColumnName() {
    return columnName;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return schemaName;
  }

  @JsonProperty("table")
  public String getTableName() {
    return tableName;
  }
}
