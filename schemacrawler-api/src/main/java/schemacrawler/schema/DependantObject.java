/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

/**
 * Represents the dependant of a database object, such as a column or an index, which are dependants
 * of a table.
 */
public interface DependantObject<P extends DatabaseObject>
    extends DatabaseObject, ContainedObject<P> {

  /**
   * Gets the name of the dependant object and the name of the parent. The parent name is not
   * fully-qualified.
   */
  String getShortName();

  boolean isParentPartial();
}
