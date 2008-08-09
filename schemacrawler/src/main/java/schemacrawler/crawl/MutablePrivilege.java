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


import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Privilege;

/**
 * Represents a privilege of a table or column.
 * 
 * @author Sualeh Fatehi
 */
final class MutablePrivilege
  extends AbstractDependantNamedObject
  implements Privilege
{

  private static final long serialVersionUID = -1117664231494271886L;

  private String grantor;
  private String grantee;
  private boolean isGrantable;

  MutablePrivilege(final String name, final DatabaseObject parent)
  {
    super(name, parent);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Privilege#getGrantee()
   */
  public String getGrantee()
  {
    return grantee;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Privilege#getGrantor()
   */
  public String getGrantor()
  {
    return grantor;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Privilege#isGrantable()
   */
  public boolean isGrantable()
  {
    return isGrantable;
  }

  void setGrantable(final boolean grantable)
  {
    isGrantable = grantable;
  }

  void setGrantee(final String grantee)
  {
    this.grantee = grantee;
  }

  void setGrantor(final String grantor)
  {
    this.grantor = grantor;
  }

}
