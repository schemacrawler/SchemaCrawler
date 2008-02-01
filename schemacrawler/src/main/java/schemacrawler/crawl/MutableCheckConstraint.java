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
   * @see schemacrawler.schema.CheckConstraint#getDefinition()
   */
  public String getDefinition()
  {
    return definition;
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

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.CheckConstraint#isInitiallyDeferred()
   */
  public boolean isInitiallyDeferred()
  {
    return initiallyDeferred;
  }

  void setDeferrable(final boolean deferrable)
  {
    this.deferrable = deferrable;
  }

  void setDefinition(final String definition)
  {
    this.definition = definition;
  }

  void setInitiallyDeferred(final boolean initiallyDeferred)
  {
    this.initiallyDeferred = initiallyDeferred;
  }

}
