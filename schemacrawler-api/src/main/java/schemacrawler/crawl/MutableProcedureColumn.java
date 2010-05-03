/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schema.ProcedureColumnType;

/**
 * Represents a column in a database procedure. Created from metadata
 * returned by a JDBC call.
 * 
 * @author Sualeh Fatehi
 */
final class MutableProcedureColumn
  extends AbstractColumn
  implements ProcedureColumn
{

  private static final long serialVersionUID = 3546361725629772857L;

  private ProcedureColumnType procedureColumnType;

  MutableProcedureColumn(final DatabaseObject parent,
                         final String name,
                         final String quoteCharacter)
  {
    super(parent, name, quoteCharacter);
  }

  /**
   * {@inheritDoc}
   * 
   * @see ProcedureColumn#getPrecision()
   */
  public int getPrecision()
  {
    return getDecimalDigits();
  }

  /**
   * {@inheritDoc}
   * 
   * @see ProcedureColumn#getProcedureColumnType()
   */
  public ProcedureColumnType getProcedureColumnType()
  {
    return procedureColumnType;
  }

  void setPrecision(final int precision)
  {
    setDecimalDigits(precision);
  }

  void setProcedureColumnType(final ProcedureColumnType procedureColumnType)
  {
    this.procedureColumnType = procedureColumnType;
  }

}
