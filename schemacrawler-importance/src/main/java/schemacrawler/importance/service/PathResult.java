/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.service;

import java.util.List;
import java.util.Objects;
import schemacrawler.importance.cache.DatabaseObjectNodeId;

/** An ordered relationship path and the confidence of the edges it required. */
public record PathResult(List<DatabaseObjectNodeId> path, boolean usesImpliedAssociations) {

  public PathResult {
    path = List.copyOf(Objects.requireNonNull(path, "No path provided"));
  }
}
