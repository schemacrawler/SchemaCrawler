package schemacrawler.tools.command.serialize.model;

import static us.fatehi.utility.Utility.isBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.schema.Table;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schemaName", "tableName", "remarks", "columns"})
public final class TableDescription {

  private final String tableName;
  private final String schemaName;
  private final List<ColumnDescription> columns;
  private final String remarks;

  public TableDescription(final Table table) {
    Objects.requireNonNull(table, "No table provided");

    this.tableName = table.getName();

    final String schema = table.getSchema().getFullName();
    if (!isBlank(schema)) {
      this.schemaName = schema;
    } else {
      this.schemaName = null;
    }

    this.columns = new ArrayList<>();

    final String remarks = table.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }
  }

  public void addColumn(final ColumnDescription column) {
    if (column != null) {
      this.columns.add(column);
    }
  }

  public List<ColumnDescription> getColumns() {
    return this.columns;
  }

  @JsonProperty("table")
  public String getTableName() {
    return this.tableName;
  }

  @JsonProperty("remarks")
  public String getRemarks() {
    return this.remarks;
  }

  @JsonProperty("schema")
  public String getSchema() {
    return this.schemaName;
  }
}
