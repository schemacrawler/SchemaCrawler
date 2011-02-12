/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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

package sf.util.clparser;


/**
 * Representation of a command-line option.
 * 
 * @author Sualeh Fatehi
 * @param <T>
 *        Option type
 */
public interface Option<T>
{

  public abstract T getDefaultValue();

  /**
   * Gets the long form of the switch for the option.
   * 
   * @return Long form of the switch
   */
  String getLongForm();

  /**
   * Gets the short form of the switch for the option.
   * 
   * @return Short form of the switch
   */
  String getShortForm();

  /**
   * Whether the option has the long form of the switch.
   * 
   * @return Whether the option has the long form of the switch
   */
  boolean hasLongForm();

  /**
   * Whether the option has the short form of the switch.
   * 
   * @return Whether the option has the short form of the switch
   */
  boolean hasShortForm();

}
