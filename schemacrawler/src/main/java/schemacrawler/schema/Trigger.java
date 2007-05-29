/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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

package schemacrawler.schema;


/**
 * Represents an trigger.
 * 
 * @author Sualeh Fatehi
 */
public interface Trigger
  extends DependantNamedObject
{

  /**
   * The WHEN clause of the trigger.
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
   * Get whether the trigger is a row trigger or a statement trigger.
   * 
   * @return Action orientation.
   */
  ActionOrientationType getActionOrientation();

  /**
   * Get the body of the trigger.
   * 
   * @return Body of the trigger.
   */
  String getActionStatement();

  /**
   * Gets the condition timing. BEFORE = the trigger is executed before
   * the triggering data manipulation operation; INSTEAD OF = the
   * trigger is executed instead of the triggering data manipulation
   * operation; AFTER = the trigger is executed after the triggering
   * data manipulation operation.
   * 
   * @return Condition timing.
   */
  ConditionTimingType getConditionTiming();

  /**
   * Gets the event manipulation type. The trigger event - INSERT,
   * DELETE, or UPDATE.
   * 
   * @return Event manipulation type
   */
  EventManipulationType getEventManipulationType();

}
