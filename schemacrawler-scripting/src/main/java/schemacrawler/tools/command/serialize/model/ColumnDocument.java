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

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"column", "remarks", "type", "referenced-column"})
public final class ColumnDocument implements Serializable {

  private static final long serialVersionUID = 5110252842937512910L;

  private final String columnName;
  private final String dataType;
  private final String remarks;
  private final ReferencedColumnDocument referencedColumn;

  public ColumnDocument(final Column column, final Column pkColumn) {
    requireNonNull(column, "No column provided");

    columnName = column.getName();

    dataType = column.getColumnDataType().getName();

    final String remarks = column.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }

    if (pkColumn == null) {
      referencedColumn = null;
    } else {
      referencedColumn = new ReferencedColumnDocument(pkColumn);
    }
  }

  @JsonProperty("column")
  public String getColumnName() {
    return columnName;
  }

  @JsonProperty("type")
  public String getDataType() {
    return dataType;
  }

  @JsonProperty("referenced-column")
  public ReferencedColumnDocument getReferencedColumn() {
    return referencedColumn;
  }

  @JsonProperty("remarks")
  public String getRemarks() {
    return remarks;
  }
}
