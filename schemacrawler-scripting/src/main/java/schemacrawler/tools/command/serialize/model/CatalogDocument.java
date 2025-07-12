/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.serialize.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"databaseProductName", "tables"})
public final class CatalogDocument implements Serializable {

  private static final long serialVersionUID = -1937966351313941597L;

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

  public static Map<AdditionalTableDetails, Boolean> allTableDetails() {
    final Map<AdditionalTableDetails, Boolean> details;
    details = new EnumMap<>(AdditionalTableDetails.class);

    for (final AdditionalTableDetails additionalTableDetails : AdditionalTableDetails.values()) {
      if (!details.containsKey(additionalTableDetails)) {
        details.put(additionalTableDetails, true);
      }
    }
    return details;
  }

  private final String databaseProductName;
  private final List<TableDocument> tables;
  private final List<RoutineDocument> routines;

  public CatalogDocument(final String databaseProductName) {
    tables = new ArrayList<>();
    routines = new ArrayList<>();
    this.databaseProductName = databaseProductName;
  }

  public void addRoutine(final RoutineDocument routine) {
    if (routine != null) {
      routines.add(routine);
    }
  }

  public void addTable(final TableDocument table) {
    if (table != null) {
      tables.add(table);
    }
  }

  @JsonProperty("db")
  public String getDatabaseProductName() {
    return databaseProductName;
  }

  @JsonProperty("routines")
  public List<RoutineDocument> getRoutines() {
    return routines;
  }

  @JsonProperty("tables")
  public List<TableDocument> getTables() {
    return tables;
  }

  public JsonNode toJson() {
    return new ObjectMapper().valueToTree(this);
  }

  @Override
  public String toString() {
    return toJson().toString();
  }
}
