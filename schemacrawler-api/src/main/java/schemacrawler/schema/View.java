/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.schema;

import java.util.Collection;
import java.util.Optional;

/** Represents a view in the database. */
public interface View extends Table {

  /**
   * Type of WITH CHECK OPTION. Is CASCADE if the original view was created by using the WITH CHECK
   * OPTION. Otherwise, NONE is returned.
   *
   * @return Check option.
   */
  CheckOptionType getCheckOption();

  /**
   * Gets tables used by the view.
   *
   * @return Tables used by the view
   */
  Collection<Table> getTableUsage();

  /**
   * Specifies whether the view is updatable.
   *
   * @return Whether the view is updatable.
   */
  boolean isUpdatable();

  /**
   * Gets a referenced table by unqualified name.
   *
   * @param name Name
   * @return Referenced table.
   */
  <T extends Table> Optional<T> lookupTable(Schema schema, String name);
}
