/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.model;

import java.util.List;

/** Top-level lint model for template rendering. */
public record LintModel(int lintCount, List<LintGroup> lintGroups) {}
