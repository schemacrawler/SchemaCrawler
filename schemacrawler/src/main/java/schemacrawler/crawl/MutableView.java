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

package schemacrawler.crawl;


import schemacrawler.schema.CheckOptionType;
import schemacrawler.schema.TableType;
import schemacrawler.schema.View;

/**
 * {@inheritDoc}
 * 
 * @author sfatehi
 */
class MutableView
  extends MutableTable
  implements View
{

  private static final long serialVersionUID = 3257290248802284852L;

  private String definition;
  private CheckOptionType checkOption;
  private boolean updatable;

  MutableView(final String catalogName,
              final String schemaName,
              final String name)
  {
    super(catalogName, schemaName, name);
  }

  void setType(final TableType type)
  {
    if (type != TableType.VIEW)
    {
      throw new UnsupportedOperationException("Cannot reset view type");
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see View#getType()
   */
  public TableType getType()
  {
    return TableType.VIEW;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.View#getDefinition()
   */
  public String getDefinition()
  {
    return definition;
  }

  void setDefinition(final String definition)
  {
    this.definition = definition;
  }

  /**
   * {@inheritDoc}
   */
  public CheckOptionType getCheckOption()
  {
    return checkOption;
  }

  void setCheckOption(final CheckOptionType checkOption)
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

  void setUpdatable(final boolean updatable)
  {
    this.updatable = updatable;
  }

}
