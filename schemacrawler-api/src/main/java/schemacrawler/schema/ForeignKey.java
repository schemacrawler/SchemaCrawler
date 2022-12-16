/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

/** Represents a foreign-key mapping to a primary key in another table. */
public interface ForeignKey extends TableReference {

  /**
   * Gets the deferrability.
   *
   * @return Deferrability
   */
  ForeignKeyDeferrability getDeferrability();

  /**
   * Gets the delete rule.
   *
   * @return Delete rule
   */
  ForeignKeyUpdateRule getDeleteRule();

  /**
   * Gets a generated specific name for databases that support non-unique foreign key names.
   *
   * @return Specific name of a foreign key
   * @deprecated
   */
  @Deprecated
  default String getSpecificName() {
    return getName();
  }

  /**
   * Gets the update rule.
   *
   * @return Update rule
   */
  ForeignKeyUpdateRule getUpdateRule();
}
