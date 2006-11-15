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


import schemacrawler.schema.CheckConstraint;
import schemacrawler.schema.NamedObject;

/**
 * Represents an index on a database table.
 */
class MutableCheckConstraint
  extends AbstractDependantNamedObject
  implements CheckConstraint
{

  private static final long serialVersionUID = 1155277343302693656L;

  private boolean deferrable;
  private boolean initiallyDeferred;
  private String definition;

  MutableCheckConstraint(final String name, final NamedObject parent)
  {
    super(name, parent);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.CheckConstraint#isDeferrable()
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
   * @see schemacrawler.schema.CheckConstraint#isInitiallyDeferred()
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
   * @see schemacrawler.schema.CheckConstraint#getDefinition()
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
