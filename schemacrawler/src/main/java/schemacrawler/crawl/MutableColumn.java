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
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Privilege;

/**
 * Represents a column in a database table or procedure. Created from
 * metadata returned by a JDBC call.
 * 
 * @author Sualeh Fatehi
 */
final class MutableColumn
  extends AbstractColumn
  implements Column
{

  private static final long serialVersionUID = 3834591019449528633L;

  private String defaultValue;
  private boolean isPartOfPrimaryKey;
  private boolean isPartOfUniqueIndex;
  private MutableColumn referencedColumn;
  private final NamedObjectList<MutablePrivilege> privileges = new NamedObjectList<MutablePrivilege>(NamedObjectSort.natural);

  MutableColumn(final String name, final DatabaseObject parent)
  {
    super(parent, name);
  }

  void addPrivilege(final MutablePrivilege privilege)
  {
    privileges.add(privilege);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getDefaultValue()
   */
  public String getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Column#getPrivileges()
   */
  public Privilege[] getPrivileges()
  {
    return privileges.getAll().toArray(new Privilege[privileges.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#getReferencedColumn()
   */
  public Column getReferencedColumn()
  {
    return referencedColumn;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfForeignKey()
   */
  public boolean isPartOfForeignKey()
  {
    return referencedColumn != null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfPrimaryKey()
   */
  public boolean isPartOfPrimaryKey()
  {
    return isPartOfPrimaryKey;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Column#isPartOfUniqueIndex()
   */
  public boolean isPartOfUniqueIndex()
  {
    return isPartOfUniqueIndex;
  }

  void setDefaultValue(final String defaultValue)
  {
    this.defaultValue = defaultValue;
  }

  void setPartOfPrimaryKey(final boolean partOfPrimaryKey)
  {
    isPartOfPrimaryKey = partOfPrimaryKey;
  }

  void setPartOfUniqueIndex(final boolean partOfUniqueIndex)
  {
    isPartOfUniqueIndex = partOfUniqueIndex;
  }

  void setReferencedColumn(final MutableColumn referencedColumn)
  {
    this.referencedColumn = referencedColumn;
  }

}
