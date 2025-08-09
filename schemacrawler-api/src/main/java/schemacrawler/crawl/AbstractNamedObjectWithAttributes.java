/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.DescribedObject;

abstract class AbstractNamedObjectWithAttributes extends AbstractNamedObject
    implements AttributedObject, DescribedObject {

  private static final String REMARKS_ATTRIBUTE = "REMARKS";

  private static final long serialVersionUID = -1486322887991472729L;

  private final Map<String, Object> attributeMap;

  /**
   * Effective Java - Item 17 - Minimize Mutability - Package-private constructors make a class
   * effectively final
   *
   * @param name Name of the named object
   */
  AbstractNamedObjectWithAttributes(final String name) {
    super(name);
    attributeMap = new ConcurrentHashMap<>();
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
    return new TreeMap<>(attributeMap);
  }

  /** {@inheritDoc} */
  @Override
  public final String getRemarks() {
    final Object remarks = attributeMap.get(REMARKS_ATTRIBUTE);
    if (remarks == null) {
      return "";
    }
    return String.valueOf(remarks);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasAttribute(final String name) {
    return attributeMap.containsKey(name);
  }

  /** {@inheritDoc} */
  @Override
  public final boolean hasRemarks() {
    return hasAttribute(REMARKS_ATTRIBUTE) && !isBlank(getRemarks());
  }

  /** {@inheritDoc} */
  @Override
  public final <T> Optional<T> lookupAttribute(final String name) {
    if (name == null) {
      return Optional.empty();
    }
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
    setAttribute(REMARKS_ATTRIBUTE, trimToEmpty(remarks));
  }

  protected final void addAttributes(final Map<String, Object> values) {
    if (values == null) {
      return;
    }
    // Check for null entries, since concurrent hash map does not allow them
    for (final Entry<String, Object> entry : values.entrySet()) {
      final String key = entry.getKey();
      final Object value = entry.getValue();
      if (key != null && value != null) {
        attributeMap.put(key, value);
      }
    }
  }
}
