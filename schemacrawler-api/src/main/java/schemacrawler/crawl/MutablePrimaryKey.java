/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.crawl;

import static schemacrawler.schema.TableConstraintType.alternate_key;
import static schemacrawler.schema.TableConstraintType.primary_key;

import java.util.EnumSet;

import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraintType;

/** Represents a primary key in a table. */
final class MutablePrimaryKey extends MutableTableConstraint implements PrimaryKey {

  private static final long serialVersionUID = -7169206178562782087L;

  static MutablePrimaryKey newAlternateKey(final Table parent, final String name) {
    return new MutablePrimaryKey(parent, name, alternate_key);
  }

  static MutablePrimaryKey newPrimaryKey(final MutableTable parent, final String name) {
    return new MutablePrimaryKey(parent, name, primary_key);
  }

  private final TableConstraintType type;

  private MutablePrimaryKey(final Table parent, final String name, final TableConstraintType type) {
    super(parent, name);
    if (!EnumSet.of(alternate_key, primary_key).contains(type)) {
      throw new IllegalArgumentException("Incorrect table constraint type provided");
    }
    this.type = type;
  }

  @Override
  public TableConstraintType getType() {
    return type;
  }

  @Override
  public boolean isDeferrable() {
    return false;
  }

  @Override
  public boolean isInitiallyDeferred() {
    return false;
  }
}
