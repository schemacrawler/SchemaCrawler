/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
 * Represents a foreign-key mapping to a primary key in another table.
 * 
 * @author Sualeh Fatehi
 */
public interface ForeignKey
  extends NamedObject
{

  /**
   * Gets the list of column pairs.
   * 
   * @return Column pairs
   */
  ForeignKeyColumnMap[] getColumnPairs();

  /**
   * Gets the deferrability.
   * 
   * @return Deferrability
   */
  ForeignKeyDeferrability getDeferrability();

  /**
   * Gets the delete rule.
   * 
   * @return Delete rule
   */
  ForeignKeyUpdateRule getDeleteRule();

  /**
   * Gets the update rule.
   * 
   * @return Update rule
   */
  ForeignKeyUpdateRule getUpdateRule();

}
