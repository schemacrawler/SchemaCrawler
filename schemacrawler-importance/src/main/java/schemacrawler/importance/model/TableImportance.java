/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model;

import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;

/** Immutable table-only metadata and topology metrics. */
public record TableImportance(
    TableTraits tableTraits, TableCounts tableCounts, TableImportanceMetrics graphMetrics) {}
