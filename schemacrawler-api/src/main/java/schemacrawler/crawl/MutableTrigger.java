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


import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;

/**
 * Represents a trigger.
 *
 * @author Sualeh Fatehi
 */
class MutableTrigger
  extends AbstractDependantObject<Table>
  implements Trigger
{

  private static final long serialVersionUID = -1619291073229701764L;

  private EventManipulationType eventManipulationType;
  private int actionOrder;
  private final StringBuilder actionCondition;
  private final StringBuilder actionStatement;
  private ActionOrientationType actionOrientation;
  private ConditionTimingType conditionTiming;

  MutableTrigger(final Table parent, final String name)
  {
    super(new TableReference(parent), name);
    // Default values
    eventManipulationType = EventManipulationType.unknown;
    actionOrientation = ActionOrientationType.unknown;
    conditionTiming = ConditionTimingType.unknown;
    actionCondition = new StringBuilder(1024);
    actionStatement = new StringBuilder(1024);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getActionCondition()
  {
    return actionCondition.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getActionOrder()
  {
    return actionOrder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ActionOrientationType getActionOrientation()
  {
    return actionOrientation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getActionStatement()
  {
    return actionStatement.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ConditionTimingType getConditionTiming()
  {
    return conditionTiming;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EventManipulationType getEventManipulationType()
  {
    return eventManipulationType;
  }

  void appendActionCondition(final String actionCondition)
  {
    if (actionCondition != null)
    {
      this.actionCondition.append(actionCondition);
    }
  }

  void appendActionStatement(final String actionStatement)
  {
    if (actionStatement != null)
    {
      this.actionStatement.append(actionStatement);
    }
  }

  void setActionOrder(final int actionOrder)
  {
    this.actionOrder = actionOrder;
  }

  void setActionOrientation(final ActionOrientationType actionOrientation)
  {
    this.actionOrientation = actionOrientation;
  }

  void setConditionTiming(final ConditionTimingType conditionTiming)
  {
    this.conditionTiming = conditionTiming;
  }

  void setEventManipulationType(final EventManipulationType eventManipulationType)
  {
    this.eventManipulationType = eventManipulationType;
  }

}
