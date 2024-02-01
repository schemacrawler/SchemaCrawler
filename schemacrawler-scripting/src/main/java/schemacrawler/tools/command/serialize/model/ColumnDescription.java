package schemacrawler.tools.command.serialize.model;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.schema.Column;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ColumnDescription {

  private final String columnName;
  private final String dataType;
  private final String remarks;
  private final ReferencedColumnDescription referencedColumn;

  public ColumnDescription(final Column column, final Column pkColumn) {
    requireNonNull(column, "No column provided");

    this.columnName = column.getName();

    this.dataType = column.getColumnDataType().getName();

    final String remarks = column.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }

    if (pkColumn == null) {
      this.referencedColumn = null;
    } else {
      this.referencedColumn = new ReferencedColumnDescription(pkColumn);
    }
  }

  @JsonProperty("type")
  public String getDataType() {
    return this.dataType;
  }

  @JsonProperty("column")
  public String getColumnName() {
    return this.columnName;
  }

  @JsonProperty("referenced-column")
  public ReferencedColumnDescription getReferencedColumn() {
    return this.referencedColumn;
  }

  @JsonProperty("remarks")
  public String getRemarks() {
    return this.remarks;
  }
}
