/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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


import static sf.util.Utility.isBlank;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;

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
    if (isBlank(specificName))
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

  @Override
  public boolean hasDefinition()
  {
    return definition.length() > 0;
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
