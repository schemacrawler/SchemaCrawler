/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;

import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

abstract class RoutinePartial
  extends AbstractDatabaseObject
  implements Routine, PartialDatabaseObject
{

  private static final long serialVersionUID = 1508498300413360531L;

  RoutinePartial(final Routine routine)
  {
    this(requireNonNull(routine, "No routine provided").getSchema(),
         routine.getName());
    addAttributes(routine.getAttributes());
  }

  RoutinePartial(final Schema schema, final String tableName)
  {
    super(schema, tableName);
  }

  @Override
  public String getDefinition()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public RoutineBodyType getRoutineBodyType()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public RoutineType getRoutineType()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public String getSpecificName()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public RoutineType getType()
  {
    throw new NotLoadedException(this);
  }

  @Override
  public boolean hasDefinition()
  {
    throw new NotLoadedException(this);
  }

}
