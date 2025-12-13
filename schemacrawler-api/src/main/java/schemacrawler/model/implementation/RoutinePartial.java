/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.crawl.NotLoadedException;

import java.io.Serial;
import java.util.Collection;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

public abstract class RoutinePartial extends AbstractDatabaseObject
    implements Routine, PartialDatabaseObject {

  @Serial private static final long serialVersionUID = 1508498300413360531L;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param schema Schema of this object
   * @param name Name of the named object
   */
  public RoutinePartial(final Schema schema, final String name) {
    super(schema, name);
  }

  @Override
  public final String getDefinition() {
    throw new NotLoadedException(this);
  }

  @Override
  public Collection<? extends DatabaseObject> getReferencedObjects() {
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
