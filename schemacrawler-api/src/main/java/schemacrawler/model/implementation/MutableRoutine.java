/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.model.implementation;


import schemacrawler.crawl.SchemaCrawler;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serial;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.RoutineParameter;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import us.fatehi.utility.CollectionsUtility;

/** Represents a database routine. Created from metadata returned by a JDBC call. */
public abstract class MutableRoutine extends AbstractDatabaseObject implements Routine {

  @Serial private static final long serialVersionUID = 3906925686089134130L;

  private transient NamedObjectKey key;
  private final String specificName;
  private RoutineBodyType routineBodyType;
  private final Collection<DatabaseObject> referencedObjects;
  private String definition;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param schema Schema of this object
   * @param name Name of the named object
   */
  public MutableRoutine(final Schema schema, final String name, final String specificName) {
    super(schema, name);
    requireNotBlank(name, "No routine name provided");

    this.specificName = specificName;
    routineBodyType = RoutineBodyType.unknown;
    referencedObjects = new HashSet<>();
    definition = "";
  }

  /**
   * {@inheritDoc}
   *
   * <p>NOTE: compareTo is not compatible with equals. equals compares the full name of a database
   * object, but compareTo uses more fields to define a "natural" sorting order. compareTo may
   * return incorrect results until the object is fully built by SchemaCrawler.
   */
  @Override
  public int compareTo(final NamedObject obj) {
    int comparison = super.compareTo(obj);

    if (obj instanceof Routine other) {
      if (comparison == 0) {
        final List<RoutineParameter<? extends Routine>> thisParameters = getParameters();
        final List<RoutineParameter<? extends Routine>> otherParameters = other.getParameters();

        comparison = CollectionsUtility.compareLists(thisParameters, otherParameters);
      }

      if (comparison == 0) {
        comparison = getSpecificName().compareTo(other.getSpecificName());
      }
    }

    return comparison;
  }

  /** {@inheritDoc} */
  @Override
  public final String getDefinition() {
    return definition;
  }

  @Override
  public Collection<DatabaseObject> getReferencedObjects() {
    return new HashSet<>(referencedObjects);
  }

  /** {@inheritDoc} */
  @Override
  public final RoutineBodyType getRoutineBodyType() {
    return routineBodyType;
  }

  @Override
  public RoutineType getRoutineType() {
    return null;
  }

  @Override
  public final String getSpecificName() {
    if (isBlank(specificName)) {
      return getName();
    }
    return specificName;
  }

  /** {@inheritDoc} */
  @Override
  public final RoutineType getType() {
    return getRoutineType();
  }

  @Override
  public final boolean hasDefinition() {
    return !isBlank(definition);
  }

  @Override
  public final NamedObjectKey key() {
    buildKey();
    return key;
  }

  public final void addReferencedObject(final DatabaseObject referencedObject) {
    if (referencedObject != null) {
      referencedObjects.add(referencedObject);
    }
  }

  public final void setDefinition(final String definition) {
    if (!hasDefinition() && !isBlank(definition)) {
      this.definition = definition;
    }
  }

  public final void setRoutineBodyType(final RoutineBodyType routineBodyType) {
    this.routineBodyType = routineBodyType;
  }

  private void buildKey() {
    if (key != null) {
      return;
    }
    key = super.key().with(specificName);
  }
}
