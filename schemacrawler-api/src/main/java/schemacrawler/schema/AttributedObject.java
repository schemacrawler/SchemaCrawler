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

import java.util.Map;
import java.util.Optional;

/** Allow attributes on objects. */
public interface AttributedObject {

  /**
   * Gets an attribute.
   *
   * @param name Attribute name.
   * @return Attribute value.
   */
  <T> T getAttribute(final String name);

  /**
   * Gets an attribute.
   *
   * @param name Attribute name.
   * @return Attribute value.
   * @throws ClassCastException If the attribute class is not assignable from the the default value
   *     class.
   */
  <T> T getAttribute(String name, T defaultValue) throws ClassCastException;

  /**
   * Gets all attributes.
   *
   * @return Map of attributes
   */
  Map<String, Object> getAttributes();

  /**
   * Checks is an attribute is available.
   *
   * @param name Attribute name.
   * @return If attribute is available.
   */
  boolean hasAttribute(String name);

  /**
   * Gets an attribute.
   *
   * @param name Attribute name.
   * @return Attribute value.
   */
  <T> Optional<T> lookupAttribute(final String name);

  /**
   * Removes an attribute.
   *
   * @param name Attribute name
   */
  void removeAttribute(String name);

  /**
   * Sets an attribute.
   *
   * @param name Attribute name
   * @param value Attribute value
   */
  <T> void setAttribute(String name, T value);
}
