/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.crawl;

import static us.fatehi.utility.Utility.isBlank;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.DescribedObject;

/**
 * Represents a named object.
 *
 * @author Sualeh Fatehi
 */
abstract class AbstractNamedObjectWithAttributes extends AbstractNamedObject
    implements AttributedObject, DescribedObject {

  private static final long serialVersionUID = -1486322887991472729L;

  private final Map<String, Object> attributeMap;
  private String remarks;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param name Name of the named object
   */
  AbstractNamedObjectWithAttributes(final String name) {
    super(name);
    attributeMap = new HashMap<>();
    remarks = "";
  }

  /** {@inheritDoc} */
  @Override
  public final <T> T getAttribute(final String name) {
    return getAttribute(name, null);
  }

  /** {@inheritDoc} */
  @Override
  public final <T> T getAttribute(final String name, final T defaultValue)
      throws ClassCastException {
    return (T) attributeMap.getOrDefault(name, defaultValue);
  }

  /** {@inheritDoc} */
  @Override
  public final Map<String, Object> getAttributes() {
    return Collections.unmodifiableMap(attributeMap);
  }

  /** {@inheritDoc} */
  @Override
  public final String getRemarks() {
    return remarks;
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasAttribute(final String name) {
    return attributeMap.containsKey(name);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasRemarks() {
    return remarks != null && !remarks.isEmpty();
  }

  /** {@inheritDoc} */
  @Override
  public final <T> Optional<T> lookupAttribute(final String name) {
    return Optional.ofNullable(getAttribute(name));
  }

  /** {@inheritDoc} */
  @Override
  public final void removeAttribute(final String name) {
    if (!isBlank(name)) {
      attributeMap.remove(name);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void setAttribute(final String name, final Object value) {
    if (!isBlank(name)) {
      if (value == null) {
        attributeMap.remove(name);
      } else {
        attributeMap.put(name, value);
      }
    }
  }

  @Override
  public final void setRemarks(final String remarks) {
    if (isBlank(remarks)) {
      this.remarks = "";
    } else {
      this.remarks = remarks;
    }
  }

  protected final void addAttributes(final Map<String, Object> values) {
    if (values != null) {
      attributeMap.putAll(values);
    }
  }
}
