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
import schemacrawler.schema.TableConstraint;

/**
 * Represents an index on a database table.
 */
class MutableTableConstraint
  extends AbstractDependantNamedObject
  implements TableConstraint
{

  private static final long serialVersionUID = 1155277343302693656L;

  private ConstraintType type;
  private boolean deferrable;
  private boolean initiallyDeferred;
  private String definition;

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.TableConstraint#isDeferrable()
   */
  public boolean isDeferrable()
  {
    return deferrable;
  }

  void setDeferrable(final boolean deferrable)
  {
    this.deferrable = deferrable;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.TableConstraint#isInitiallyDeferred()
   */
  public boolean isInitiallyDeferred()
  {
    return initiallyDeferred;
  }

  void setInitiallyDeferred(final boolean initiallyDeferred)
  {
    this.initiallyDeferred = initiallyDeferred;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.TableConstraint#getType()
   */
  public ConstraintType getType()
  {
    return type;
  }

  void setType(final ConstraintType type)
  {
    this.type = type;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.TableConstraint#getDefinition()
   */
  public String getDefinition()
  {
    return definition;
  }

  void setDefinition(final String definition)
  {
    this.definition = definition;
  }

}
