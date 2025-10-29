/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import static java.sql.DatabaseMetaData.tableIndexClustered;
import static java.sql.DatabaseMetaData.tableIndexHashed;
import static java.sql.DatabaseMetaData.tableIndexOther;
import static java.sql.DatabaseMetaData.tableIndexStatistic;

import us.fatehi.utility.IdentifiedEnum;

/** An enumeration wrapper around index types. */
public enum IndexType implements IdentifiedEnum {
  unknown(-1),
  statistic(tableIndexStatistic),
  clustered(tableIndexClustered),
  hashed(tableIndexHashed),
  other(tableIndexOther);

  private final int id;

  IndexType(final int id) {
    this.id = id;
  }

  /** {@inheritDoc} */
  @Override
  public int id() {
    return id;
  }
}
