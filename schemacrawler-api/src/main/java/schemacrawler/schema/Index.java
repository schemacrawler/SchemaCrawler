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


import java.util.List;

/**
 * Represents an index on a database table.
 * 
 * @author Sualeh Fatehi
 */
public interface Index
  extends DependantObject<Table>, TypedObject<IndexType>
{

  /**
   * Gets the cardinality. When the index type is statistic, then this
   * is the number of rows in the table; otherwise, it is the number of
   * unique values in the index.
   * 
   * @return Cardinality
   */
  int getCardinality();

  /**
   * Gets the list of columns in ordinal order.
   * 
   * @return Columns of the table.
   */
  List<IndexColumn> getColumns();

  /**
   * Gets the pages. When the index type is statistic, then this is the
   * number of pages used for the table, otherwise it is the number of
   * pages used for the current index.
   * 
   * @return Pages
   */
  int getPages();

  /**
   * If the index is unique.
   * 
   * @return If the index is unique
   */
  boolean isUnique();

}
