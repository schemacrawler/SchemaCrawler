/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

import java.util.Collection;
import us.fatehi.utility.property.Property;

/** Represents a JDBC driver property, and it's value. */
public interface JdbcDriverProperty extends Property {

  /**
   * Gets the array of possible values if the value for the field <code>DriverPropertyInfo.value
   * </code> may be selected from a particular set of values.
   *
   * @return Available choices for the value of a property
   */
  Collection<String> getChoices();

  /**
   * Gets the the current value of the property, based on a combination of the information supplied
   * to the method <code>getPropertyInfo</code>, the Java environment, and the driver-supplied
   * default values. This field may be null if no value is known.
   *
   * @return Value of the property
   */
  @Override
  String getValue();

  /**
   * The <code>required</code> field is <code>true</code> if a value must be supplied for this
   * property during <code>Driver.connect</code> and <code>false</code> otherwise.
   *
   * @return Whether the property is required
   */
  boolean isRequired();
}
