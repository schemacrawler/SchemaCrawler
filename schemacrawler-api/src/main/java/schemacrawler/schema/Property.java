/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.Objects.compare;

import java.io.Serializable;
import java.util.Comparator;

public interface Property extends Serializable, Comparable<Property> {

  Comparator<Property> comparator =
      nullsLast(comparing(Property::getName, String.CASE_INSENSITIVE_ORDER));

  @Override
  default int compareTo(final Property otherProperty) {
    return compare(this, otherProperty, comparator);
  }

  /**
   * Gets the description of the property.
   *
   * @return Description
   */
  String getDescription();

  /**
   * Gets the name of the property.
   *
   * @return Name
   */
  String getName();

  /**
   * Gets the value of the property.
   *
   * @return Value
   */
  Object getValue();
}
