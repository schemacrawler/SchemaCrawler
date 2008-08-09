/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Trigger;

/**
 * Represents a trigger.
 * 
 * @author Sualeh Fatehi
 */
class MutableTrigger
  extends AbstractDependantObject
  implements Trigger
{

  private static final long serialVersionUID = -1619291073229701764L;

  private EventManipulationType eventManipulationType;
  private int actionOrder;
  private String actionCondition;
  private String actionStatement;
  private ActionOrientationType actionOrientation;
  private ConditionTimingType conditionTiming;

  MutableTrigger(final DatabaseObject parent, final String name)
  {
    super(parent, name);
    // Default values
    eventManipulationType = EventManipulationType.unknown;
    actionOrientation = ActionOrientationType.unknown;
    conditionTiming = ConditionTimingType.unknown;
  }

  /**
   * {@inheritDoc}
   */
  public String getActionCondition()
  {
    return actionCondition;
  }

  /**
   * {@inheritDoc}
   */
  public int getActionOrder()
  {
    return actionOrder;
  }

  /**
   * {@inheritDoc}
   */
  public ActionOrientationType getActionOrientation()
  {
    return actionOrientation;
  }

  /**
   * {@inheritDoc}
   */
  public String getActionStatement()
  {
    return actionStatement;
  }

  /**
   * {@inheritDoc}
   */
  public ConditionTimingType getConditionTiming()
  {
    return conditionTiming;
  }

  /**
   * {@inheritDoc}
   */
  public EventManipulationType getEventManipulationType()
  {
    return eventManipulationType;
  }

  void setActionCondition(final String actionCondition)
  {
    this.actionCondition = actionCondition;
  }

  void setActionOrder(final int actionOrder)
  {
    this.actionOrder = actionOrder;
  }

  void setActionOrientation(final ActionOrientationType actionOrientation)
  {
    this.actionOrientation = actionOrientation;
  }

  void setActionStatement(final String actionStatement)
  {
    this.actionStatement = actionStatement;
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
