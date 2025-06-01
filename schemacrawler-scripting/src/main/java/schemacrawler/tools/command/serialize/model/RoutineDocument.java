package schemacrawler.tools.command.serialize.model;

import static schemacrawler.tools.command.serialize.model.AdditionalRoutineDetails.DEFINIITION;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import static us.fatehi.utility.Utility.trimToEmpty;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineParameter;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "routine", "type", "remarks", "parameters", "definition"})
public final class RoutineDocument implements Serializable {

  private static final long serialVersionUID = 1873929712139211255L;

  public static Map<AdditionalRoutineDetails, Boolean> allRoutineDetails() {
    final Map<AdditionalRoutineDetails, Boolean> details;
    details = new EnumMap<>(AdditionalRoutineDetails.class);

    for (final AdditionalRoutineDetails additionalRoutineDetails :
        AdditionalRoutineDetails.values()) {
      if (!details.containsKey(additionalRoutineDetails)) {
        details.put(additionalRoutineDetails, true);
      }
    }
    return details;
  }

  private final String schemaName;
  private final String routineName;
  private final String routineType;
  private final String remarks;
  private final List<RoutineParameterDocument> parameters;
  private final String definition;

  RoutineDocument(
      final Routine routine, final Map<AdditionalRoutineDetails, Boolean> routineDetails) {
    Objects.requireNonNull(routine, "No routine provided");
    final Map<AdditionalRoutineDetails, Boolean> details = defaults(routineDetails);

    final String schemaName = routine.getSchema().getFullName();
    this.schemaName = trimToEmpty(schemaName);

    routineName = routine.getName();
    routineType = routine.getRoutineType().toString();

    parameters = new ArrayList<>();
    for (final RoutineParameter routineParameter : routine.getParameters()) {
      final RoutineParameterDocument parameterDocument =
          new RoutineParameterDocument(routineParameter);
      parameters.add(parameterDocument);
    }

    if (routine.hasRemarks()) {
      final String remarks = routine.getRemarks();
      this.remarks = trimToEmpty(remarks);
    } else {
      remarks = null;
    }

    if (details.get(DEFINIITION) && routine.hasDefinition()) {
      definition = routine.getDefinition();
    } else {
      definition = null;
    }
  }

  public String getDefinition() {
    return definition;
  }

  public List<RoutineParameterDocument> getParameters() {
    return parameters;
  }

  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("routine")
  public String getRoutineName() {
    return routineName;
  }

  @JsonProperty("type")
  public String getRoutineType() {
    return routineType;
  }

  @JsonProperty("schema")
  public String getSchema() {
    return schemaName;
  }

  public JsonNode toJson() {
    return new ObjectMapper().valueToTree(this);
  }

  @Override
  public String toString() {
    return toJson().toString();
  }

  private Map<AdditionalRoutineDetails, Boolean> defaults(
      final Map<AdditionalRoutineDetails, Boolean> routineDetails) {
    final Map<AdditionalRoutineDetails, Boolean> details;
    if (routineDetails == null) {
      details = new EnumMap<>(AdditionalRoutineDetails.class);
    } else {
      details = routineDetails;
    }

    for (final AdditionalRoutineDetails additionalRoutineDetails :
        AdditionalRoutineDetails.values()) {
      if (!details.containsKey(additionalRoutineDetails)) {
        details.put(additionalRoutineDetails, false);
      }
    }
    return details;
  }
}
