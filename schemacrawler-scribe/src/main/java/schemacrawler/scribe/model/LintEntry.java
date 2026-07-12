/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.model;

import schemacrawler.schema.Table;

/** One lint row for template rendering. */
public record LintEntry(String objectName, Table table, String severity, String message) {}
