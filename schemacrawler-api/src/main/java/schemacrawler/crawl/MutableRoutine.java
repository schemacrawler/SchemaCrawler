/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static us.fatehi.utility.Utility.isBlank;

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
import us.fatehi.utility.CompareUtility;

/** Represents a database routine. Created from metadata returned by a JDBC call. */
abstract class MutableRoutine extends AbstractDatabaseObject implements Routine {

  private static final long serialVersionUID = 3906925686089134130L;

  private transient NamedObjectKey key;
  private final String specificName;
  private RoutineBodyType routineBodyType;
  private final Collection<DatabaseObject> referencedObjects;
  private final StringBuffer definition;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param schema Schema of this object
   * @param name Name of the named object
   */
  MutableRoutine(final Schema schema, final String name, final String specificName) {
    super(schema, name);
    this.specificName = specificName;
    routineBodyType = RoutineBodyType.unknown;
    referencedObjects = new HashSet<>();
    definition = new StringBuffer();
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

    if (obj instanceof Routine) {
      final Routine other = (Routine) obj;
      if (comparison == 0) {
        final List<RoutineParameter<? extends Routine>> thisParameters = getParameters();
        final List<RoutineParameter<? extends Routine>> otherParameters = other.getParameters();

        comparison = CompareUtility.compareLists(thisParameters, otherParameters);
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
    return definition.toString();
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
    return definition.length() > 0;
  }

  @Override
  public final NamedObjectKey key() {
    buildKey();
    return key;
  }

  void addReferencedObject(final DatabaseObject referencedObject) {
    if (referencedObject != null) {
      referencedObjects.add(referencedObject);
    }
  }

  final void appendDefinition(final String definition) {
    if (definition != null) {
      this.definition.append(definition);
    }
  }

  final void setRoutineBodyType(final RoutineBodyType routineBodyType) {
    this.routineBodyType = routineBodyType;
  }

  private void buildKey() {
    if (key != null) {
      return;
    }
    key = super.key().with(specificName);
  }
}
