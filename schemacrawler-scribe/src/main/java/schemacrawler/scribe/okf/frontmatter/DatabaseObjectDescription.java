/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import java.net.URI;

public record DatabaseObjectDescription(
    String simpleTypeName,
    String completeType,
    String schemaName,
    String name,
    String fullName,
    String description,
    String intro,
    URI resource) {

  public DatabaseObjectDescription() {
    this(null, null, null, null, null, null, null, null);
  }
}
