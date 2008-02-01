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


import schemacrawler.schema.Column;
import schemacrawler.schema.ForeignKeyColumnMap;
import schemacrawler.schema.NamedObject;

/**
 * {@inheritDoc}
 */
final class MutableForeignKeyColumnMap
  extends AbstractDependantNamedObject
  implements ForeignKeyColumnMap
{

  private static final long serialVersionUID = 3689073962672273464L;

  private Column foreignKeyColumn;
  private Column primaryKeyColumn;
  private int keySequence;

  MutableForeignKeyColumnMap(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final NamedObject obj)
  {
    final ForeignKeyColumnMap other = (ForeignKeyColumnMap) obj;
    int comparison = 0;

    if (comparison == 0)
    {
      comparison = getKeySequence() - other.getKeySequence();
    }
    // Note: For the primary key and foreign key columns, compare by
    // name.
    if (comparison == 0)
    {
      comparison = getPrimaryKeyColumn().getName().compareTo(other
        .getPrimaryKeyColumn().getName());
    }
    if (comparison == 0)
    {
      comparison = getForeignKeyColumn().getName().compareTo(other
        .getForeignKeyColumn().getName());
    }

    return comparison;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getForeignKeyColumn()
   */
  public Column getForeignKeyColumn()
  {
    return foreignKeyColumn;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getKeySequence()
   */
  public int getKeySequence()
  {
    return keySequence;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.ForeignKeyColumnMap#getPrimaryKeyColumn()
   */
  public Column getPrimaryKeyColumn()
  {
    return primaryKeyColumn;
  }

  void setForeignKeyColumn(final Column foreignKeyColumn)
  {
    this.foreignKeyColumn = foreignKeyColumn;
  }

  void setKeySequence(final int keySequence)
  {
    this.keySequence = keySequence;
  }

  void setPrimaryKeyColumn(final Column primaryKeyColumn)
  {
    this.primaryKeyColumn = primaryKeyColumn;
  }

}
