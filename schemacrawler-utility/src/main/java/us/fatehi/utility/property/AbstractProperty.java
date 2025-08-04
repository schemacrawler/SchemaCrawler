/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.property;

import static java.util.Objects.requireNonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractProperty implements Property {

  private static final long serialVersionUID = -7150431683440256142L;

  private final PropertyName propertyName;
  private final Serializable value;

  protected AbstractProperty(final PropertyName name, final Serializable value) {
    propertyName = requireNonNull(name, "No property name provided");
    if (value != null && value.getClass().isArray()) {
      this.value = (Serializable) Arrays.asList((Object[]) value);
    } else {
      this.value = value;
    }
  }

  @Override
  public final boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof Property)) {
      return false;
    }

    final Property other = (Property) obj;
    boolean nameEquals;
    if (obj instanceof AbstractProperty) {
      nameEquals = Objects.equals(propertyName, ((AbstractProperty) obj).propertyName);
    } else /* if (obj instanceof Property) */ {
      nameEquals = Objects.equals(propertyName.getName(), other.getName());
    }
    return nameEquals && Objects.equals(value, other.getValue());
  }

  /** {@inheritDoc} */
  @Override
  public final String getName() {
    return propertyName.getName();
  }

  /** {@inheritDoc} */
  @Override
  public final String getDescription() {
    return propertyName.getDescription();
  }

  /** {@inheritDoc} */
  @Override
  public Serializable getValue() {
    return value;
  }

  @Override
  public final int hashCode() {
    return Objects.hash(propertyName, value);
  }

  @Override
  public String toString() {
    return getName() + " = " + getValue();
  }
}
