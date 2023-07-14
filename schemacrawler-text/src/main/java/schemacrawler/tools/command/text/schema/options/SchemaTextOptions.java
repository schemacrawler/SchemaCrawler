/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.text.schema.options;

import java.util.EnumMap;
import java.util.Map;
import schemacrawler.tools.text.options.BaseTextOptions;

public class SchemaTextOptions extends BaseTextOptions {

  private final boolean isAlphabeticalSortForForeignKeys;
  private final boolean isAlphabeticalSortForIndexes;
  private final boolean isHideRemarks;
  private final boolean isShowOrdinalNumbers;
  private final boolean isShowStandardColumnTypeNames;
  private final boolean isHideTableRowCounts;
  private final Map<HideDependantDatabaseObjectsType, Boolean> hideDatabaseObjects;
  private final Map<HideDatabaseObjectNamesType, Boolean> hideNames;

  protected SchemaTextOptions(
      final BaseSchemaTextOptionsBuilder<?, ? extends SchemaTextOptions> builder) {
    super(builder);

    isAlphabeticalSortForForeignKeys = builder.isAlphabeticalSortForForeignKeys;
    isAlphabeticalSortForIndexes = builder.isAlphabeticalSortForIndexes;
    isHideRemarks = builder.isHideRemarks;
    isShowOrdinalNumbers = builder.isShowOrdinalNumbers;
    isShowStandardColumnTypeNames = builder.isShowStandardColumnTypeNames;
    isHideTableRowCounts = builder.isHideTableRowCounts;

    hideDatabaseObjects = new EnumMap<>(HideDependantDatabaseObjectsType.class);
    for (final HideDependantDatabaseObjectsType databaseObjectsType :
        HideDependantDatabaseObjectsType.values()) {
      hideDatabaseObjects.put(
          databaseObjectsType,
          builder.hideDependantDatabaseObjects.getOrDefault(databaseObjectsType, false));
    }
    hideNames = new EnumMap<>(HideDatabaseObjectNamesType.class);
    for (final HideDatabaseObjectNamesType databaseObjectNamesType :
        HideDatabaseObjectNamesType.values()) {
      hideNames.put(
          databaseObjectNamesType, builder.hideNames.getOrDefault(databaseObjectNamesType, false));
    }
  }

  public boolean is(final HideDatabaseObjectNamesType key) {
    return hideNames.getOrDefault(key, false);
  }

  public boolean is(final HideDependantDatabaseObjectsType key) {
    return hideDatabaseObjects.getOrDefault(key, false);
  }

  public boolean isAlphabeticalSortForForeignKeys() {
    return isAlphabeticalSortForForeignKeys;
  }

  public boolean isAlphabeticalSortForIndexes() {
    return isAlphabeticalSortForIndexes;
  }

  public boolean isHideRemarks() {
    return isHideRemarks;
  }

  public boolean isHideTableRowCounts() {
    return isHideTableRowCounts;
  }

  public boolean isShowOrdinalNumbers() {
    return isShowOrdinalNumbers;
  }

  public boolean isShowStandardColumnTypeNames() {
    return isShowStandardColumnTypeNames;
  }
}
