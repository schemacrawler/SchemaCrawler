/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.util.Set;

/** Represents a trigger. */
public interface Trigger extends DependantObject<Table> {

  /**
   * Gets the WHEN clause of the trigger.
   *
   * @return Action condition.
   */
  String getActionCondition();

  /**
   * Gets the action order.
   *
   * @return Action order.
   */
  int getActionOrder();

  /**
   * Gets whether the trigger is a row trigger or a statement trigger.
   *
   * @return Action orientation.
   */
  ActionOrientationType getActionOrientation();

  /**
   * Gets the body of the trigger.
   *
   * @return Body of the trigger.
   */
  String getActionStatement();

  /**
   * Gets the condition timing. BEFORE = the trigger is executed before the triggering data
   * manipulation operation; INSTEAD OF = the trigger is executed instead of the triggering data
   * manipulation operation; AFTER = the trigger is executed after the triggering data manipulation
   * operation.
   *
   * @return Condition timing.
   */
  ConditionTimingType getConditionTiming();

  /**
   * Gets the event manipulation types. The trigger event - INSERT, DELETE, or UPDATE.
   *
   * @return Event manipulation types
   */
  Set<EventManipulationType> getEventManipulationTypes();
}
