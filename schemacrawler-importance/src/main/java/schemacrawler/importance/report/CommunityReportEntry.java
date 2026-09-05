/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.report;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.List;
import java.util.UUID;
import schemacrawler.importance.model.DatabaseObjectNodeId;

/** One detected domain community entry in an importance report. */
public record CommunityReportEntry(
    UUID id,
    DatabaseObjectNodeId anchorNodeId,
    String anchorTableFullName,
    int totalCommunitySize,
    List<DatabaseObjectNodeId> memberNodeIds,
    List<String> memberTableFullNames) {

  public CommunityReportEntry {
    requireNonNull(id, "No community id provided");
    requireNonNull(anchorNodeId, "No anchor node id provided");
    requireNotBlank(anchorTableFullName, "No anchor table full name provided");
    requireNonNull(memberNodeIds, "No member node ids provided");
    requireNonNull(memberTableFullNames, "No member table full names provided");
    memberNodeIds = List.copyOf(memberNodeIds);
    memberTableFullNames = List.copyOf(memberTableFullNames);
    if (totalCommunitySize < 0) {
      throw new IllegalArgumentException("Total community size cannot be negative");
    }
  }
}
