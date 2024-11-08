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

package schemacrawler.crawl;

import java.util.EnumSet;
import java.util.Set;

import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;

/** Represents a trigger. */
class MutableTrigger extends AbstractDependantObject<Table> implements Trigger {

  private static final long serialVersionUID = -1619291073229701764L;
  private final StringBuffer actionCondition;
  private final StringBuffer actionStatement;
  private int actionOrder;
  private ActionOrientationType actionOrientation;
  private ConditionTimingType conditionTiming;
  private Set<EventManipulationType> eventManipulationType;

  MutableTrigger(final Table parent, final String name) {
    super(new TablePointer(parent), name);
    // Default values
    eventManipulationType = EnumSet.noneOf(EventManipulationType.class);
    actionOrientation = ActionOrientationType.unknown;
    conditionTiming = ConditionTimingType.unknown;
    actionCondition = new StringBuffer();
    actionStatement = new StringBuffer();
  }

  /** {@inheritDoc} */
  @Override
  public String getActionCondition() {
    return actionCondition.toString();
  }

  /** {@inheritDoc} */
  @Override
  public int getActionOrder() {
    return actionOrder;
  }

  /** {@inheritDoc} */
  @Override
  public ActionOrientationType getActionOrientation() {
    return actionOrientation;
  }

  /** {@inheritDoc} */
  @Override
  public String getActionStatement() {
    return actionStatement.toString();
  }

  /** {@inheritDoc} */
  @Override
  public ConditionTimingType getConditionTiming() {
    return conditionTiming;
  }

  /** {@inheritDoc} */
  @Override
  public Set<EventManipulationType> getEventManipulationTypes() {
    return EnumSet.copyOf(eventManipulationType);
  }

  void appendActionCondition(final String actionCondition) {
    if (actionCondition != null) {
      this.actionCondition.append(actionCondition);
    }
  }

  void appendActionStatement(final String actionStatement) {
    if (actionStatement != null) {
      this.actionStatement.append(actionStatement);
    }
  }

  void setActionOrder(final int actionOrder) {
    this.actionOrder = actionOrder;
  }

  void setActionOrientation(final ActionOrientationType actionOrientation) {
    this.actionOrientation = actionOrientation;
  }

  void setConditionTiming(final ConditionTimingType conditionTiming) {
    this.conditionTiming = conditionTiming;
  }

  void setEventManipulationTypes(final Set<EventManipulationType> eventManipulationTypes) {
    if (eventManipulationTypes == null) {
      this.eventManipulationType.add(EventManipulationType.unknown);
    } else {
      this.eventManipulationType.addAll(eventManipulationTypes);
    }
  }
}
