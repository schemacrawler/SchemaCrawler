/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;

import org.jspecify.annotations.NonNull;

/** Options controlling how schema information is loaded. */
public record LoadOptions(@NonNull SchemaInfoLevel schemaInfoLevel, int maxThreads)
    implements Options {

  public LoadOptions {
    schemaInfoLevel = requireNonNull(schemaInfoLevel, "No schema info level provided");
  }
}
