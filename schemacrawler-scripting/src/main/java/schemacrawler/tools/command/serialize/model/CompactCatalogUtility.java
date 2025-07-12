/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.serialize.model;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;

public final class CompactCatalogUtility {

  private final EnumMap<AdditionalTableDetails, Boolean> additionalTableDetails;
  private final EnumMap<AdditionalRoutineDetails, Boolean> additionalRoutineDetails;

  public CompactCatalogUtility() {
    additionalTableDetails = new EnumMap<>(AdditionalTableDetails.class);
    additionalRoutineDetails = new EnumMap<>(AdditionalRoutineDetails.class);
  }

  public CatalogDocument createCatalogDocument(final Catalog catalog) {
    requireNonNull(catalog, "No catalog provided");

    final CatalogDocument catalogDocument =
        new CatalogDocument(catalog.getDatabaseInfo().getDatabaseProductName());
    for (final Table table : catalog.getTables()) {
      final TableDocument tableDocument = getTableDocument(table);
      catalogDocument.addTable(tableDocument);
    }
    for (final Routine routine : catalog.getRoutines()) {
      final RoutineDocument routineDocument = getRoutineDocument(routine);
      catalogDocument.addRoutine(routineDocument);
    }
    return catalogDocument;
  }

  public RoutineDocument getRoutineDocument(final Routine routine) {
    requireNonNull(routine, "No routine provided");
    final RoutineDocument routineDocument = new RoutineDocument(routine, additionalRoutineDetails);
    return routineDocument;
  }

  public TableDocument getTableDocument(final Table table) {
    requireNonNull(table, "No table provided");
    final TableDocument tableDocument = new TableDocument(table, additionalTableDetails);
    return tableDocument;
  }

  public CompactCatalogUtility withAdditionalRoutineDetails(
      final Collection<AdditionalRoutineDetails> withAdditionalRoutineDetails) {
    if (withAdditionalRoutineDetails == null || withAdditionalRoutineDetails.isEmpty()) {
      return this;
    }
    for (final AdditionalRoutineDetails additionalRoutineDetail : withAdditionalRoutineDetails) {
      additionalRoutineDetails.put(additionalRoutineDetail, true);
    }

    return this;
  }

  public CompactCatalogUtility withAdditionalRoutineDetails(
      final Map<AdditionalRoutineDetails, Boolean> withAdditionalRoutineDetails) {
    if (withAdditionalRoutineDetails == null || withAdditionalRoutineDetails.isEmpty()) {
      return this;
    }
    additionalRoutineDetails.clear();
    additionalRoutineDetails.putAll(withAdditionalRoutineDetails);

    return this;
  }

  public CompactCatalogUtility withAdditionalTableDetails(
      final Collection<AdditionalTableDetails> withAdditionalTableDetails) {
    if (withAdditionalTableDetails == null || withAdditionalTableDetails.isEmpty()) {
      return this;
    }
    for (final AdditionalTableDetails additionalTableDetail : withAdditionalTableDetails) {
      additionalTableDetails.put(additionalTableDetail, true);
    }
    return this;
  }

  public CompactCatalogUtility withAdditionalTableDetails(
      final Map<AdditionalTableDetails, Boolean> withAdditionalTableDetails) {
    if (withAdditionalTableDetails == null || withAdditionalTableDetails.isEmpty()) {
      return this;
    }
    additionalTableDetails.clear();
    additionalTableDetails.putAll(withAdditionalTableDetails);

    return this;
  }
}
