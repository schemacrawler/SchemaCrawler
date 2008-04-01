/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
