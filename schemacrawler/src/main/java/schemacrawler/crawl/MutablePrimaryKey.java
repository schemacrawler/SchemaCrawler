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


import schemacrawler.schema.ConstraintType;
import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.TableConstraint;

/**
 * Primary key.
 * 
 * @author sfatehi
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

  MutablePrimaryKey(String name, NamedObject parent)
  {
    super(name, parent);
  }

  /**
   * {@inheritDoc}
   */
  public TableConstraint asTableConstraint()
    throws SchemaCrawlerException
  {
    final MutableTableConstraint constraint = (MutableTableConstraint) super
      .asTableConstraint();
    constraint.setType(ConstraintType.PRIMARY_KEY);
    return constraint;
  }

}
