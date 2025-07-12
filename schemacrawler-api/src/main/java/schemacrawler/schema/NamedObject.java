/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.io.Serializable;

/** Represents a named object. */
public interface NamedObject extends Serializable, Comparable<NamedObject> {

  /**
   * Getter for fully qualified name of object.
   *
   * @return Fully qualified of the object
   */
  String getFullName();

  /**
   * Getter for name of object.
   *
   * @return Name of the object
   */
  String getName();

  /** A value guaranteed to be unique in the database for this object. */
  NamedObjectKey key();
}
