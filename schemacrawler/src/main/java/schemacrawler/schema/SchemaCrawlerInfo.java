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
public interface SchemaCrawlerInfo
  extends Serializable
{

  /**
   * Gets the SchemaCrawler about text.
   * 
   * @return SchemaCrawler about text
   */
  String getSchemaCrawlerAbout();

  /**
   * Gets the name of the SchemaCrawler product.
   * 
   * @return Name of the SchemaCrawler product
   */
  String getSchemaCrawlerProductName();

  /**
   * Gets the SchemaCrawler version.
   * 
   * @return SchemaCrawler version
   */
  String getSchemaCrawlerVersion();

  /**
   * Gets all system properties.
   * 
   * @return Map of properties
   */
  Map<String, String> getSystemProperties();

}
