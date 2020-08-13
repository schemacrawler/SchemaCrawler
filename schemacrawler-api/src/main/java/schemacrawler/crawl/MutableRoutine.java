/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static us.fatehi.utility.Utility.isBlank;

import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineBodyType;
import schemacrawler.schema.RoutineParameter;
import schemacrawler.schema.RoutineType;
import schemacrawler.schema.Schema;
import us.fatehi.utility.CompareUtility;

/**
 * Represents a database routine. Created from metadata returned by a JDBC
 * call.
 *
 * @author Sualeh Fatehi
 */
abstract class MutableRoutine
  extends AbstractDatabaseObject
  implements Routine
{
  private static final long serialVersionUID = 3906925686089134130L;
  private final StringBuilder definition;
  private RoutineBodyType routineBodyType;
  private String specificName;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private
   * constructors make a class effectively final
   *
   * @param schema
   *   Schema of this object
   * @param name
   *   Name of the named object
   */
  MutableRoutine(final Schema schema, final String name)
  {
    super(schema, name);
    routineBodyType = RoutineBodyType.unknown;
    definition = new StringBuilder();
  }

  @Override
  public int compareTo(final NamedObject obj)
  {
    int comparison = super.compareTo(obj);

    if (obj instanceof Routine)
    {
      final Routine other = (Routine) obj;
      if (comparison == 0)
      {
        final List<RoutineParameter<? extends Routine>> thisParameters =
          getParameters();
        final List<RoutineParameter<? extends Routine>> otherParameters =
          other.getParameters();

        comparison =
          CompareUtility.compareLists(thisParameters, otherParameters);
      }

      if (comparison == 0)
      {
        comparison = this
          .getSpecificName()
          .compareTo(other.getSpecificName());
      }
    }

    return comparison;
  }

  @Override
  public final List<String> toUniqueLookupKey()
  {
    // Make a defensive copy
    final List<String> lookupKey = new ArrayList<>(super.toUniqueLookupKey());
    lookupKey.add(specificName);
    return lookupKey;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getDefinition()
  {
    return definition.toString();
  }

  @Override
  public final boolean hasDefinition()
  {
    return definition.length() > 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final RoutineBodyType getRoutineBodyType()
  {
    return routineBodyType;
  }

  final void setRoutineBodyType(final RoutineBodyType routineBodyType)
  {
    this.routineBodyType = routineBodyType;
  }

  @Override
  public RoutineType getRoutineType()
  {
    return null;
  }

  @Override
  public final String getSpecificName()
  {
    if (isBlank(specificName))
    {
      return getName();
    }
    else
    {
      return specificName;
    }
  }

  final void setSpecificName(final String specificName)
  {
    this.specificName = specificName;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final RoutineType getType()
  {
    return getRoutineType();
  }

  final void appendDefinition(final String definition)
  {
    if (definition != null)
    {
      this.definition.append(definition);
    }
  }

}
