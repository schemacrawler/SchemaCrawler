package schemacrawler.tools.command.serialize.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schemaName", "tableName", "columnName"})
public final class ReferencedColumnDescription {

  private final String tableName;
  private final String schemaName;
  private final String columnName;

  public ReferencedColumnDescription(final Column column) {
    requireNonNull(column, "No column provided");

    Table table = column.getParent();

    this.tableName = table.getName();

    final String schema = table.getSchema().getFullName();
    if (!isBlank(schema)) {
      this.schemaName = schema;
    } else {
      this.schemaName = null;
    }

    this.columnName = column.getName();
  }

  @JsonProperty("table")
  public String getTableName() {
    return this.tableName;
  }

  @JsonProperty("schema")
  public String getSchemaName() {
    return this.schemaName;
  }

  @JsonProperty("column")
  public String getColumnName() {
    return this.columnName;
  }
}
