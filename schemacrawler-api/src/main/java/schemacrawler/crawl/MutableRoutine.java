/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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


import schemacrawler.schema.Procedure;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import sf.util.Utility;

/**
 * Represents a database routine. Created from metadata returned by a
 * JDBC call.
 * 
 * @author Sualeh Fatehi
 */
abstract class MutableRoutine
  extends AbstractDatabaseObject
  implements Routine
{

  private static final long serialVersionUID = 3906925686089134130L;

  private String specificName;
  private RoutineBodyType routineBodyType;
  private final StringBuilder definition;

  MutableRoutine(final Schema schema, final String name)
  {
    super(schema, name);
    routineBodyType = RoutineBodyType.unknown;
    definition = new StringBuilder();
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getDefinition()
   */
  @Override
  public String getDefinition()
  {
    return definition.toString();
  }

  @Override
  public String getLookupKey()
  {
    final String lookupKey = super.getLookupKey();
    if (Utility.isBlank(specificName))
    {
      return lookupKey;
    }
    else
    {
      return getSchema().getFullName() + "." + specificName;
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see Procedure#getRoutineBodyType()
   */
  @Override
  public RoutineBodyType getRoutineBodyType()
  {
    return routineBodyType;
  }

  @Override
  public String getSpecificName()
  {
    return specificName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.TypedObject#getType()
   */
  @Override
  public final RoutineType getType()
  {
    return getRoutineType();
  }

  void appendDefinition(final String definition)
  {
    if (definition != null)
    {
      this.definition.append(definition);
    }
  }

  void setRoutineBodyType(final RoutineBodyType routineBodyType)
  {
    this.routineBodyType = routineBodyType;
  }

  void setSpecificName(final String specificName)
  {
    this.specificName = specificName;
  }

}
