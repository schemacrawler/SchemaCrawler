/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import java.util.Collection;

public interface ReferencingObject {

  /**
   * Gets the referenced objects.
   *
   * @return Referenced objects.
   */
  Collection<? extends DatabaseObject> getReferencedObjects();
}
