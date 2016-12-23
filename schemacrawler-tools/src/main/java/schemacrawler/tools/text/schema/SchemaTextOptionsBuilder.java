/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.text.schema;


import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;

public class SchemaTextOptionsBuilder
  extends BaseTextOptionsBuilder<SchemaTextOptions>
{

  private static final String SHOW_ORDINAL_NUMBERS = SCHEMACRAWLER_FORMAT_PREFIX
                                                     + "show_ordinal_numbers";
  private static final String SHOW_STANDARD_COLUMN_TYPE_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                                + "show_standard_column_type_names";
  private static final String SHOW_ROW_COUNTS = SCHEMACRAWLER_FORMAT_PREFIX
                                                + "show_row_counts";

  private static final String HIDE_PRIMARY_KEY_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                       + "hide_primarykey_names";
  private static final String HIDE_FOREIGN_KEY_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                       + "hide_foreignkey_names";
  private static final String HIDE_INDEX_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                 + "hide_index_names";
  private static final String HIDE_CONSTRAINT_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                      + "hide_constraint_names";
  private static final String HIDE_TRIGGER_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                   + "hide_trigger_names";
  private static final String HIDE_ROUTINE_SPECIFIC_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                            + "hide_routine_specific_names";
  private static final String HIDE_REMARKS = SCHEMACRAWLER_FORMAT_PREFIX
                                             + "hide_remarks";
  private static final String HIDE_WEAK_ASSOCIATIONS = SCHEMACRAWLER_FORMAT_PREFIX
                                                       + "hide_weak_associations";

  private static final String SC_SORT_ALPHABETICALLY_TABLE_INDEXES = SCHEMACRAWLER_FORMAT_PREFIX
                                                                     + "sort_alphabetically.table_indexes";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS = SCHEMACRAWLER_FORMAT_PREFIX
                                                                         + "sort_alphabetically.table_foreignkeys";

  public SchemaTextOptionsBuilder()
  {
    this(new SchemaTextOptions());
  }

  public SchemaTextOptionsBuilder(final SchemaTextOptions options)
  {
    super(options);
  }

  @Override
  public SchemaTextOptionsBuilder fromConfig(final Config map)
  {
    if (map == null)
    {
      return this;
    }
    super.fromConfig(map);

    final Config config = new Config(map);

    options.setShowStandardColumnTypeNames(config
      .getBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES));
    options.setShowOrdinalNumbers(config.getBooleanValue(SHOW_ORDINAL_NUMBERS));
    options.setShowRowCounts(config.getBooleanValue(SHOW_ROW_COUNTS));

    options
      .setHideForeignKeyNames(config.getBooleanValue(HIDE_FOREIGN_KEY_NAMES));
    options
      .setHidePrimaryKeyNames(config.getBooleanValue(HIDE_PRIMARY_KEY_NAMES));
    options.setHideIndexNames(config.getBooleanValue(HIDE_INDEX_NAMES));
    options.setHideTriggerNames(config.getBooleanValue(HIDE_TRIGGER_NAMES));
    options.setHideRoutineSpecificNames(config
      .getBooleanValue(HIDE_ROUTINE_SPECIFIC_NAMES));
    options
      .setHideConstraintNames(config.getBooleanValue(HIDE_CONSTRAINT_NAMES));
    options.setHideRemarks(config.getBooleanValue(HIDE_REMARKS));
    options
      .setHideWeakAssociations(config.getBooleanValue(HIDE_WEAK_ASSOCIATIONS));

    options.setAlphabeticalSortForForeignKeys(config
      .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS));
    options.setAlphabeticalSortForIndexes(config
      .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES));

    return this;
  }

  public SchemaTextOptionsBuilder noRemarks(final boolean value)
  {
    options.setHideRemarks(value);
    return this;
  }

  public SchemaTextOptionsBuilder weakAssociations(final boolean value)
  {
    options.setHideWeakAssociations(!value);
    return this;
  }

  public SchemaTextOptionsBuilder portableNames(final boolean value)
  {
    options.setHideConstraintNames(value);
    options.setHideForeignKeyNames(value);
    options.setHideIndexNames(value);
    options.setHidePrimaryKeyNames(value);
    options.setHideTriggerNames(value);
    options.setHideRoutineSpecificNames(value);
    options.setShowUnqualifiedNames(value);

    return this;
  }

  public SchemaTextOptionsBuilder sortInOut(final boolean value)
  {
    options.setAlphabeticalSortForRoutineColumns(value);
    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = super.toConfig();

    config.setBooleanValue(SHOW_STANDARD_COLUMN_TYPE_NAMES,
                           options.isShowStandardColumnTypeNames());
    config.setBooleanValue(SHOW_ORDINAL_NUMBERS,
                           options.isShowOrdinalNumbers());
    config.setBooleanValue(SHOW_ROW_COUNTS, options.isShowRowCounts());

    config.setBooleanValue(HIDE_FOREIGN_KEY_NAMES,
                           options.isHideForeignKeyNames());
    config.setBooleanValue(HIDE_PRIMARY_KEY_NAMES,
                           options.isHidePrimaryKeyNames());
    config.setBooleanValue(HIDE_INDEX_NAMES, options.isHideIndexNames());
    config.setBooleanValue(HIDE_TRIGGER_NAMES, options.isHideTriggerNames());
    config.setBooleanValue(HIDE_ROUTINE_SPECIFIC_NAMES,
                           options.isHideRoutineSpecificNames());
    config.setBooleanValue(HIDE_CONSTRAINT_NAMES,
                           options.isHideTableConstraintNames());
    config.setBooleanValue(HIDE_REMARKS, options.isHideRemarks());
    config.setBooleanValue(HIDE_WEAK_ASSOCIATIONS,
                           options.isHideWeakAssociations());

    config.setBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS,
                           options.isAlphabeticalSortForForeignKeys());
    config.setBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES,
                           options.isAlphabeticalSortForIndexes());

    return config;
  }

}
