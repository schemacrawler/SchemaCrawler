/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.requireNotBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.time.Instant;

public record OkfVerifiedRecord(String by, Instant at) {
  public OkfVerifiedRecord {
    by = requireNotBlank(trimToEmpty(by), "No verified.by value provided");
    at = requireNonNull(at, "No verified.at value provided");
  }
}
