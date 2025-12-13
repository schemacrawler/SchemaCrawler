/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;


import schemacrawler.model.implementation.AbstractNamedObjectWithAttributes;
import java.io.Serial;
import schemacrawler.schema.DatabaseUser;

public final class ImmutableDatabaseUser extends AbstractNamedObjectWithAttributes
    implements DatabaseUser {

  /** */
  @Serial private static final long serialVersionUID = -2454810590096151457L;

  ImmutableDatabaseUser(final String name) {
    super(name);
  }
}
