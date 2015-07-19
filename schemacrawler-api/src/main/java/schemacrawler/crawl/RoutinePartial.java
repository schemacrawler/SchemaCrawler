/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
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
    this(requireNonNull(routine, "No routine provided").getSchema(), routine
      .getName());
    addAttributes(routine.getAttributes());
  }

  RoutinePartial(final Schema schema, final String tableName)
  {
    super(schema, tableName);
  }

  @Override
  public String getDefinition()
  {
    throw new NotLoadedException();
  }

  @Override
  public RoutineBodyType getRoutineBodyType()
  {
    throw new NotLoadedException();
  }

  @Override
  public RoutineType getRoutineType()
  {
    throw new NotLoadedException();
  }

  @Override
  public String getSpecificName()
  {
    throw new NotLoadedException();
  }

  @Override
  public RoutineType getType()
  {
    throw new NotLoadedException();
  }

  @Override
  public boolean hasDefinition()
  {
    throw new NotLoadedException();
  }

}
