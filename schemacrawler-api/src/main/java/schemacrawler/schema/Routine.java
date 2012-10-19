/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.util.List;

public interface Routine
  extends DatabaseObject
{

  /**
   * Gets a column by name.
   * 
   * @param name
   *        Name
   * @return Column of the routine
   */
  RoutineColumn<? extends Routine> getColumn(String name);

  /**
   * Gets the list of columns in ordinal order.
   * 
   * @return Columns of the routine
   */
  List<? extends RoutineColumn<? extends Routine>> getColumns();

  /**
   * Gets the definition.
   * 
   * @return Definition
   */
  String getDefinition();

  /**
   * Gets the routine type.
   * 
   * @return Routine type
   */
  RoutineReturnType getReturnType();

  /**
   * Gets the type of the routine body.
   * 
   * @return Routine body type
   */
  RoutineBodyType getRoutineBodyType();

  /**
   * Gets the routine type.
   * 
   * @return Routine type.
   */
  RoutineType getRoutineType();

  /**
   * The name which uniquely identifies this routine within its schema.
   * 
   * @return Specific name.
   */
  String getSpecificName();

}
