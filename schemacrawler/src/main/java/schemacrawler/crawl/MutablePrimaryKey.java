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


import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.PrimaryKey;

/**
 * Primary key.
 * 
 * @author Sualeh Fatehi
 */
class MutablePrimaryKey
  extends MutableIndex
  implements PrimaryKey
{

  private static final long serialVersionUID = -7169206178562782087L;

  /**
   * Copies information from an index.
   * 
   * @param index
   *        Index
   * @return Primary key
   */
  static MutablePrimaryKey fromIndex(final Index index)
  {
    final MutablePrimaryKey pk = new MutablePrimaryKey(index.getName(), index
      .getParent());
    pk.setCardinality(index.getCardinality());
    pk.setPages(index.getPages());
    pk.setRemarks(index.getRemarks());
    pk.setSortSequence(index.getSortSequence());
    pk.setType(index.getType());
    pk.setUnique(index.isUnique());
    return pk;
  }

  MutablePrimaryKey(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

}
