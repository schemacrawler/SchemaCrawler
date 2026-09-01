/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.report;

import schemacrawler.importance.cache.DatabaseObjectNodeId;
import schemacrawler.importance.cache.TableImportanceMetrics;
import schemacrawler.tools.utility.TableCounts;
import schemacrawler.tools.utility.TableTraits;

/** One table or view in an importance report. */
public record ImportanceReportEntry(
    DatabaseObjectNodeId nodeId,
    String tableFullName,
    TableImportanceMetrics graphMetrics,
    TableCounts tableCounts,
    TableTraits tableTraits) {}
