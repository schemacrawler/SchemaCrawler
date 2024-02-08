package schemacrawler.tools.command.serialize.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Table;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "table", "remarks", "columns", "dependents"})
public final class TableDescription implements Serializable {

  private static final long serialVersionUID = 1873929712139211255L;

  private final String tableName;
  private final String schemaName;
  private final List<ColumnDescription> columns;
  private final String remarks;
  private final Collection<TableDescription> dependentTables;

  public TableDescription(final Table table) {
    Objects.requireNonNull(table, "No table provided");

    tableName = table.getName();

    final String schema = table.getSchema().getFullName();
    if (!isBlank(schema)) {
      schemaName = schema;
    } else {
      schemaName = null;
    }

    columns = new ArrayList<>();

    final String remarks = table.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }

    dependentTables = new ArrayList<>();
  }

  public List<ColumnDescription> getColumns() {
    return columns;
  }

  @JsonProperty("dependents")
  public Collection<TableDescription> getDependentTables() {
    return dependentTables;
  }

  @JsonProperty("remarks")
  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("schema")
  public String getSchema() {
    return schemaName;
  }

  @JsonProperty("table")
  public String getTableName() {
    return tableName;
  }

  public String toJson() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      return super.toString();
    }
  }

  @Override
  public String toString() {
    return toJson();
  }

  void addColumn(final ColumnDescription column) {
    if (column != null) {
      columns.add(column);
    }
  }

  void addDependentTable(final TableDescription dependentTable) {
    if (dependentTable != null) {
      dependentTables.add(dependentTable);
    }
  }
}
