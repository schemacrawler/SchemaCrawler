/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.loader.weakassociations;

import static schemacrawler.utility.MetaDataUtility.constructForeignKeyName;

import java.util.AbstractMap;

import schemacrawler.crawl.WeakAssociation;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Table;

public final class ProposedWeakAssociation
    extends AbstractMap.SimpleImmutableEntry<Column, Column> {

  private static final long serialVersionUID = 24677218335455928L;

  ProposedWeakAssociation(final Column key, final Column value) {
    super(key, value);
  }

  ProposedWeakAssociation(final ColumnReference columnReference) {
    super(columnReference.getPrimaryKeyColumn(), columnReference.getForeignKeyColumn());
  }

  public void create() {

    final Column pkColumn = getKey();
    final Column fkColumn = getValue();

    final String foreignKeyName = constructForeignKeyName(pkColumn, fkColumn);

    final WeakAssociation weakAssociation = new WeakAssociation(foreignKeyName);
    weakAssociation.addColumnReference(pkColumn, fkColumn);

    fkColumn.getParent().addWeakAssociation(weakAssociation);
    pkColumn.getParent().addWeakAssociation(weakAssociation);
  }

  public boolean isValid() {

    final Column pkColumn = getKey();
    final Column fkColumn = getValue();
    if (pkColumn == null || fkColumn == null) {
      return false;
    }

    final boolean isPkColumnPartial = pkColumn instanceof PartialDatabaseObject;
    final boolean isFkColumnPartial = fkColumn instanceof PartialDatabaseObject;
    if (isFkColumnPartial && isPkColumnPartial) {
      return false;
    }

    if (pkColumn.equals(fkColumn)) {
      return false;
    }

    final Table pkTable = pkColumn.getParent();
    final Table fkTable = fkColumn.getParent();
    if ((fkColumn.isPartOfPrimaryKey() || fkColumn.isPartOfUniqueIndex())
        && pkTable.compareTo(fkTable) > 0) {
      return false;
    }

    final ColumnDataType fkColumnType = fkColumn.getColumnDataType();
    final ColumnDataType pkColumnType = pkColumn.getColumnDataType();
    final boolean isValid =
        fkColumnType.getJavaSqlType().getName().equals(pkColumnType.getJavaSqlType().getName());
    return isValid;
  }
}
