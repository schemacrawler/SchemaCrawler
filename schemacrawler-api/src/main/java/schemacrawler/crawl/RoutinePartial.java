/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

abstract class RoutinePartial extends AbstractDatabaseObject
    implements Routine, PartialDatabaseObject {

  private static final long serialVersionUID = 1508498300413360531L;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param schema Schema of this object
   * @param name Name of the named object
   */
  RoutinePartial(final Schema schema, final String name) {
    super(schema, name);
  }

  @Override
  public final String getDefinition() {
    throw new NotLoadedException(this);
  }

  @Override
  public final RoutineBodyType getRoutineBodyType() {
    throw new NotLoadedException(this);
  }

  @Override
  public final String getSpecificName() {
    throw new NotLoadedException(this);
  }

  /** {@inheritDoc} */
  @Override
  public final RoutineType getType() {
    return getRoutineType();
  }

  @Override
  public final boolean hasDefinition() {
    throw new NotLoadedException(this);
  }
}
