/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.io.Serial;
import java.util.EnumSet;
import java.util.Set;
import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;

/** Represents a trigger. */
class MutableTrigger extends AbstractDependantObject<Table> implements Trigger {

  @Serial private static final long serialVersionUID = -1619291073229701764L;
  private final StringBuffer actionCondition;
  private final StringBuffer actionStatement;
  private int actionOrder;
  private ActionOrientationType actionOrientation;
  private ConditionTimingType conditionTiming;
  private final Set<EventManipulationType> eventManipulationType;

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
      eventManipulationType.add(EventManipulationType.unknown);
    } else {
      eventManipulationType.addAll(eventManipulationTypes);
    }
  }
}
