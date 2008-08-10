/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
   * Gets the catalog name.
   * 
   * @return Catalog name
   */
  String getCatalogName();

  /**
   * Gets the column data types defined by the RDBMS system.
   * 
   * @return Column data types
   */
  ColumnDataType[] getColumnDataTypes();

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
   * Gets the schema pattern.
   * 
   * @return Schema pattern
   */
  String getSchemaPattern();

}
