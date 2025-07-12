/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.serialize.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Trigger;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({
  "name",
  "table",
  "remarks",
  "primary-key",
  "columns",
  "indexes",
  "attributes",
  "definition"
})
public final class TriggerDocument implements Serializable {

  private static final long serialVersionUID = 1873929712139211255L;

  private final String triggerName;
  private final String actionCondition;
  private final String actionStatement;
  private final int actionOrder;
  private final String actionOrientation;
  private final String conditionTiming;
  private final List<String> eventManipulationType;

  TriggerDocument(final Trigger trigger) {
    Objects.requireNonNull(trigger, "No table provided");

    triggerName = trigger.getName();
    actionCondition = trigger.getActionCondition();
    actionStatement = trigger.getActionStatement();
    actionOrder = trigger.getActionOrder();
    actionOrientation = trigger.getActionOrientation().toString();
    conditionTiming = trigger.getConditionTiming().toString();
    eventManipulationType =
        trigger.getEventManipulationTypes().stream()
            .map(EventManipulationType::toString)
            .collect(Collectors.toList());
  }

  public String getActionCondition() {
    return actionCondition;
  }

  public int getActionOrder() {
    return actionOrder;
  }

  public String getActionOrientation() {
    return actionOrientation;
  }

  public String getActionStatement() {
    return actionStatement;
  }

  public String getConditionTiming() {
    return conditionTiming;
  }

  public List<String> getEventManipulationType() {
    return eventManipulationType;
  }

  @JsonProperty("name")
  public String getTriggerName() {
    return triggerName;
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      return super.toString();
    }
  }
}
