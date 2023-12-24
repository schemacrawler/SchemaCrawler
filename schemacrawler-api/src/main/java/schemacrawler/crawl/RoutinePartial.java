/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
