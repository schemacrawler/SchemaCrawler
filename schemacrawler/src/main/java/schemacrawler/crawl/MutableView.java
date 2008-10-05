/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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
 * Represents a view in the database.
 * 
 * @author Sualeh Fatehi
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

  /**
   * {@inheritDoc}
   */
  public CheckOptionType getCheckOption()
  {
    return checkOption;
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

  /**
   * {@inheritDoc}
   * 
   * @see View#getType()
   */
  @Override
  public TableType getType()
  {
    return TableType.view;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isUpdatable()
  {
    return updatable;
  }

  void setCheckOption(final CheckOptionType checkOption)
  {
    this.checkOption = checkOption;
  }

  void setDefinition(final String definition)
  {
    this.definition = definition;
  }

  @Override
  void setType(final TableType type)
  {
    if (type != TableType.view)
    {
      throw new UnsupportedOperationException("Cannot reset view type");
    }
  }

  void setUpdatable(final boolean updatable)
  {
    this.updatable = updatable;
  }

}
