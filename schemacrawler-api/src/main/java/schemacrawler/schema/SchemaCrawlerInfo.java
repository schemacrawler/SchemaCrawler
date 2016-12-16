/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.Serializable;

/**
 * SchemaCrawler information.
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

}
