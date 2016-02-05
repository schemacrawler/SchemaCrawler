/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import java.util.Map;
import java.util.Optional;

public interface AttributedObject
{

  /**
   * Gets an attribute.
   *
   * @param name
   *        Attribute name.
   * @return Attribute value.
   */
  default <T> T getAttribute(final String name)
  {
    return getAttribute(name, (T) null);
  }

  /**
   * Gets an attribute.
   *
   * @param name
   *        Attribute name.
   * @return Attribute value.
   */
  <T> T getAttribute(String name, T defaultValue);

  /**
   * Gets all attributes.
   *
   * @return Map of attributes
   */
  Map<String, Object> getAttributes();

  /**
   * Checks is an attribute is available.
   *
   * @param name
   *        Attribute name.
   * @return If attribute is available.
   */
  boolean hasAttribute(String name);

  /**
   * Gets an attribute.
   *
   * @param name
   *        Attribute name.
   * @return Attribute value.
   */
  default <T> Optional<T> lookupAttribute(final String name)
  {
    return Optional.of(getAttribute(name));
  }

  /**
   * Removes an attribute.
   *
   * @param name
   *        Attribute name
   */
  void removeAttribute(String name);

  /**
   * Sets an attribute.
   *
   * @param name
   *        Attribute name
   * @param value
   *        Attribute value
   */
  <T> void setAttribute(String name, T value);

}
