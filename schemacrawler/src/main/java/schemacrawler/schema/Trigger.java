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
