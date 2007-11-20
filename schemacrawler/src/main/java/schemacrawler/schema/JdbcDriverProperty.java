/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


/**
 * Represents a JDBC driver property, and it's value.
 * 
 * @author sfatehi
 */
public interface JdbcDriverProperty
  extends NamedObject
{

  /**
   * An array of possible values if the value for the field
   * <code>DriverPropertyInfo.value</code> may be selected from a
   * particular set of values.
   */
  String[] getChoices();

  /**
   * A brief description of the property.
   */
  String getDescription();

  /**
   * The <code>value</code> field specifies the current value of the
   * property, based on a combination of the information supplied to the
   * method <code>getPropertyInfo</code>, the Java environment, and
   * the driver-supplied default values. This field may be null if no
   * value is known.
   */
  String getValue();

  /**
   * Whether this property has any possible choices.
   */
  boolean hasChoices();

  /**
   * The <code>required</code> field is <code>true</code> if a value
   * must be supplied for this property during
   * <code>Driver.connect</code> and <code>false</code> otherwise.
   */
  boolean isRequired();

}
