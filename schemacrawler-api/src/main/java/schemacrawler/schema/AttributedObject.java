/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
