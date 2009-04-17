/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import java.io.Serializable;
import java.util.Map;

/**
 * Database and connection information.
 * 
 * @author Sualeh Fatehi
 */
public interface DatabaseInfo
  extends Serializable
{

  /**
   * Gets the name of the database.
   * 
   * @return Name of the database
   */
  String getCatalogName();

  /**
   * Gets the name of the RDBMS vendor and product.
   * 
   * @return Name of the RDBMS vendor and product
   */
  String getProductName();

  /**
   * Gets the RDBMS product version.
   * 
   * @return RDBMS product version
   */
  String getProductVersion();

  /**
   * Gets all database properties.
   * 
   * @return Map of properties
   */
  Map<String, Object> getProperties();

  /**
   * Gets a database property.
   * 
   * @param name
   *        Name of the property
   * @return Value of the property
   */
  Object getProperty(String name);

  /**
   * Gets the column data types defined by the RDBMS system, by name.
   * 
   * @return Column data type
   */
  ColumnDataType getSystemColumnDataType(String name);

  /**
   * Gets the column data types defined by the RDBMS system.
   * 
   * @return Column data types
   */
  ColumnDataType[] getSystemColumnDataTypes();

}
