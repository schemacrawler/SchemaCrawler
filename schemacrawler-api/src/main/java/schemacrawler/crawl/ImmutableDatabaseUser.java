/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import schemacrawler.schema.DatabaseUser;

final class ImmutableDatabaseUser extends AbstractNamedObjectWithAttributes
    implements DatabaseUser {

  /** */
  private static final long serialVersionUID = -2454810590096151457L;

  ImmutableDatabaseUser(final String name) {
    super(name);
  }
}
