/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.text.schema;


import java.util.Map;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.base.BaseTextOptionsBuilder;

public class SchemaTextOptionsBuilder
  extends BaseTextOptionsBuilder<SchemaTextOptions>
{

  private static final String SHOW_ORDINAL_NUMBERS = SCHEMACRAWLER_FORMAT_PREFIX
                                                     + "show_ordinal_numbers";
  private static final String SHOW_STANDARD_COLUMN_TYPE_NAMES = SCHEMACRAWLER_FORMAT_PREFIX
                                                                + "show_standard_column_type_names";

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
  public SchemaTextOptionsBuilder fromConfig(final Map<String, String> map)
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

    options.setAlphabeticalSortForForeignKeys(config
      .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS));
    options.setAlphabeticalSortForIndexes(config
      .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES));

    return this;
  }

  public SchemaTextOptionsBuilder hideRemarks()
  {
    options.setHideRemarks(true);
    return this;
  }

  public SchemaTextOptionsBuilder noInfo()
  {
    options.setNoInfo(true);
    return this;
  }

  public SchemaTextOptionsBuilder portableNames()
  {
    options.setHideConstraintNames(true);
    options.setHideForeignKeyNames(true);
    options.setHideIndexNames(true);
    options.setHidePrimaryKeyNames(true);
    options.setHideTriggerNames(true);
    options.setHideRoutineSpecificNames(true);
    options.setShowUnqualifiedNames(true);
    return this;
  }

  public SchemaTextOptionsBuilder sortInOut()
  {
    options.setAlphabeticalSortForRoutineColumns(true);
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

    config.setBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS,
                           options.isAlphabeticalSortForForeignKeys());
    config.setBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES,
                           options.isAlphabeticalSortForIndexes());

    return config;
  }

}
