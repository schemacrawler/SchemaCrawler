/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.crawl;


import schemacrawler.schema.Routine;
import schemacrawler.schema.RoutineColumn;

/**
 * Represents a column in a database routine. Created from metadata
 * returned by a JDBC call.
 * 
 * @author Sualeh Fatehi
 */
abstract class MutableRoutineColumn<R extends Routine>
  extends AbstractColumn<R>
  implements RoutineColumn<R>
{

  private static final long serialVersionUID = 3546361725629772857L;

  MutableRoutineColumn(final R parent, final String name)
  {
    super(parent, name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getPrecision()
  {
    return getDecimalDigits();
  }

  void setPrecision(final int precision)
  {
    setDecimalDigits(precision);
  }

}
