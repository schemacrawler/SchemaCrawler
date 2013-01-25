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


import schemacrawler.schema.CheckOptionType;
import schemacrawler.schema.Schema;
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

  private final StringBuilder definition;
  private CheckOptionType checkOption;
  private boolean updatable;

  MutableView(final Schema schema, final String name)
  {
    super(schema, name);
    definition = new StringBuilder();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CheckOptionType getCheckOption()
  {
    return checkOption;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.View#getDefinition()
   */
  @Override
  public String getDefinition()
  {
    return definition.toString();
  }

  /**
   * {@inheritDoc}
   * 
   * @see View#getTableType()
   */
  @Override
  public TableType getTableType()
  {
    return TableType.view;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isUpdatable()
  {
    return updatable;
  }

  void appendDefinition(final String definition)
  {
    if (definition != null)
    {
      this.definition.append(definition);
    }
  }

  void setCheckOption(final CheckOptionType checkOption)
  {
    this.checkOption = checkOption;
  }

  @Override
  void setTableType(final TableType type)
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
