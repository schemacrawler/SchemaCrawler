package schemacrawler.tools.command.serialize.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.RoutineParameter;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"parameter", "remarks", "type"})
public final class RoutineParameterDocument implements Serializable {

  private static final long serialVersionUID = 5110252842937512910L;

  private final String routineParameterName;
  private final String dataType;
  private final String remarks;

  public RoutineParameterDocument(final RoutineParameter routineParameter) {
    requireNonNull(routineParameter, "No routine parameter provided");

    routineParameterName = routineParameter.getName();

    dataType = routineParameter.getColumnDataType().getName();

    final String remarks = routineParameter.getRemarks();
    if (!isBlank(remarks)) {
      this.remarks = remarks;
    } else {
      this.remarks = null;
    }
  }

  @JsonProperty("parameter")
  public String getColumnName() {
    return routineParameterName;
  }

  @JsonProperty("type")
  public String getDataType() {
    return dataType;
  }

  @JsonProperty("remarks")
  public String getRemarks() {
    return remarks;
  }
}
