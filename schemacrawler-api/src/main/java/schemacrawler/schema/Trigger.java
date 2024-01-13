/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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
