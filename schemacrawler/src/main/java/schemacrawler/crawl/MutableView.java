/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import schemacrawler.schema.CheckOptionType;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableType;
import schemacrawler.schema.View;

/**
 * {@inheritDoc}
 * 
 * @author sfatehi
 */
final class MutableView
  extends MutableTable
  implements View
{

  private static final long serialVersionUID = 4323360093470059631L;
  
  private CheckOptionType checkOption;
  private boolean updatable;

  /**
   * {@inheritDoc}
   * OVERRIDE.
   * 
   * @see Table#getType()
   */
  public TableType getType()
  {
    return TableType.VIEW;
  }
  
  /**
   * {@inheritDoc}
   */
  public CheckOptionType getCheckOption()
  {
    return checkOption;
  }

  void setCheckOption(CheckOptionType checkOption)
  {
    this.checkOption = checkOption;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isUpdatable()
  {
    return updatable;
  }

  void setUpdatable(boolean updatable)
  {
    this.updatable = updatable;
  }

}
