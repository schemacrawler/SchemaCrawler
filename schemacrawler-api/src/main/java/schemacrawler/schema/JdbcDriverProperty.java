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
