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


import java.util.Arrays;
import java.util.Properties;

import schemacrawler.crawl.NamedObjectList.NamedObjectSort;
import schemacrawler.schema.TableType;

/**
 * SchemaCrawler options.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerOptions
  implements Options
{

  private static final String DEFAULT_TABLE_TYPES = "TABLE,VIEW";

  private static final long serialVersionUID = -3557794862382066029L;

  private static final String SC_SHOW_STORED_PROCEDURES = "schemacrawler.show_stored_procedures";
  private static final String SC_COLUMN_PATTERN_EXCLUDE = "schemacrawler.column.pattern.exclude";
  private static final String SC_COLUMN_PATTERN_INCLUDE = "schemacrawler.column.pattern.include";
  private static final String SC_TABLE_PATTERN_EXCLUDE = "schemacrawler.table.pattern.exclude";
  private static final String SC_TABLE_PATTERN_INCLUDE = "schemacrawler.table.pattern.include";
  private static final String SC_SORT_ALPHABETICALLY_PROCEDURE_COLUMNS = "schemacrawler.sort_alphabetically.procedure_columns";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_INDICES = "schemacrawler.sort_alphabetically.table_indices";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS = "schemacrawler.sort_alphabetically.table_foreignkeys";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_COLUMNS = "schemacrawler.sort_alphabetically.table_columns";
  private static final String SC_TABLE_TYPES = "schemacrawler.table_types";

  private static final String OTHER_SCHEMA_PATTERN = "schemapattern";

  private static TableType[] copyTableTypes(final TableType[] tableTypes)
  {
    final TableType[] tableTypesCopy = new TableType[tableTypes.length];
    System.arraycopy(tableTypes, 0, tableTypesCopy, 0, tableTypes.length);
    return tableTypesCopy;
  }

  private TableType[] tableTypes;

  private boolean showStoredProcedures;

  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;

  private NamedObjectSort tableColumnComparator;
  private NamedObjectSort tableForeignKeyComparator;
  private NamedObjectSort tableIndexComparator;

  private NamedObjectSort procedureColumnComparator;

  private SchemaInfoLevel schemaInfoLevel;
  private InformationSchemaViews informationSchemaViews;
  private String schemaPattern;

  /**
   * Default options.
   */
  public SchemaCrawlerOptions()
  {
    tableTypes = TableType.valueOf(DEFAULT_TABLE_TYPES.split(","));

    showStoredProcedures = false;

    informationSchemaViews = new InformationSchemaViews();
    schemaPattern = null;

    tableInclusionRule = new InclusionRule();
    columnInclusionRule = new InclusionRule();

    tableColumnComparator = NamedObjectSort.natural;
    tableForeignKeyComparator = NamedObjectSort.natural;
    tableIndexComparator = NamedObjectSort.natural;
    procedureColumnComparator = NamedObjectSort.natural;
  }

  /**
   * Options from properties.
   * 
   * @param config
   *        Configuration properties
   */
  public SchemaCrawlerOptions(final Config config)
  {
    this(config, null);
  }

  /**
   * Options from properties.
   * 
   * @param config
   *        Configuration properties
   * @param partition
   *        Partition for information schema
   */
  public SchemaCrawlerOptions(final Config config, final String partition)
  {

    final String tableTypesString = config.getStringValue(SC_TABLE_TYPES,
                                                          DEFAULT_TABLE_TYPES);
    tableTypes = TableType.valueOf(tableTypesString.split(","));

    showStoredProcedures = config.getBooleanValue(SC_SHOW_STORED_PROCEDURES);

    final Config partitionedConfig = config.partition(partition);
    informationSchemaViews = new InformationSchemaViews(partitionedConfig);
    schemaPattern = partitionedConfig
      .getStringValue(OTHER_SCHEMA_PATTERN, null);

    tableInclusionRule = new InclusionRule(config
                                             .getStringValue(SC_TABLE_PATTERN_INCLUDE,
                                                             InclusionRule.INCLUDE_ALL),
                                           config
                                             .getStringValue(SC_TABLE_PATTERN_EXCLUDE,
                                                             InclusionRule.EXCLUDE_NONE));
    columnInclusionRule = new InclusionRule(config
                                              .getStringValue(SC_COLUMN_PATTERN_INCLUDE,
                                                              InclusionRule.INCLUDE_ALL),
                                            config
                                              .getStringValue(SC_COLUMN_PATTERN_EXCLUDE,
                                                              InclusionRule.EXCLUDE_NONE));

    // comparators
    tableColumnComparator = getComparator(SC_SORT_ALPHABETICALLY_TABLE_COLUMNS,
                                          config);
    tableForeignKeyComparator = getComparator(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS,
                                              config);
    tableIndexComparator = getComparator(SC_SORT_ALPHABETICALLY_TABLE_INDICES,
                                         config);
    procedureColumnComparator = getComparator(SC_SORT_ALPHABETICALLY_PROCEDURE_COLUMNS,
                                              config);
  }

  /**
   * Options from properties.
   * 
   * @param properties
   *        Configuration properties
   */
  public SchemaCrawlerOptions(final Properties properties)
  {
    this(new Config(properties));
  }

  /**
   * Gets the column inclusion rule.
   * 
   * @return Column inclusion rule.
   */
  public InclusionRule getColumnInclusionRule()
  {
    return columnInclusionRule;
  }

  /**
   * Gets the information schema views.
   * 
   * @return Information schema views.
   */
  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  /**
   * Gets the schema information level, identifying to what level the
   * schema should be crawled.
   * 
   * @return Schema information level.
   */
  public SchemaInfoLevel getSchemaInfoLevel()
  {
    if (schemaInfoLevel == null)
    {
      return SchemaInfoLevel.basic();
    }
    else
    {
      return schemaInfoLevel;
    }
  }

  /**
   * Gets the schema pattern.
   * 
   * @return Schema name pattern
   * @see SchemaCrawlerOptions#setSchemaPattern(String)
   */
  public String getSchemaPattern()
  {
    return schemaPattern;
  }

  /**
   * Gets the table inclusion rule.
   * 
   * @return Table inclusion rule.
   */
  public InclusionRule getTableInclusionRule()
  {
    return tableInclusionRule;
  }

  /**
   * Get the table types.
   * 
   * @return Table types
   */
  public TableType[] getTableTypes()
  {
    final TableType[] tableTypesCopy = copyTableTypes(tableTypes);
    return tableTypesCopy;
  }

  /**
   * Whether foreign keys are alphabetically sorted.
   * 
   * @return Whether foreign keys are alphabetically sorted
   */
  public boolean isAlphabeticalSortForForeignKeys()
  {
    return tableForeignKeyComparator != null
           && tableForeignKeyComparator == NamedObjectSort.alphabetical;
  }

  /**
   * Whether indexes are alphabetically sorted.
   * 
   * @return Whether indexes are alphabetically sorted
   */
  public boolean isAlphabeticalSortForIndexes()
  {
    return tableIndexComparator != null
           && tableIndexComparator == NamedObjectSort.alphabetical;
  }

  /**
   * Whether procedure columns are alphabetically sorted.
   * 
   * @return Whether procedure columns are alphabetically sorted
   */
  public boolean isAlphabeticalSortForProcedureColumns()
  {
    return procedureColumnComparator != null
           && procedureColumnComparator == NamedObjectSort.alphabetical;
  }

  /**
   * Whether table columns are alphabetically sorted.
   * 
   * @return Whether table columns are alphabetically sorted
   */
  public boolean isAlphabeticalSortForTableColumns()
  {
    return tableColumnComparator != null
           && tableColumnComparator == NamedObjectSort.alphabetical;
  }

  /**
   * Whether stored procedures are output.
   * 
   * @return Whether stored procedures are output
   */
  public boolean isShowStoredProcedures()
  {
    return showStoredProcedures;
  }

  /**
   * Sets whether foreign keys should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForForeignKeys(final boolean alphabeticalSort)
  {
    tableForeignKeyComparator = getComparator(alphabeticalSort);
  }

  /**
   * Sets whether indexes should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForIndexes(final boolean alphabeticalSort)
  {
    tableIndexComparator = getComparator(alphabeticalSort);
  }

  /**
   * Sets whether procedure columns should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForProcedureColumns(final boolean alphabeticalSort)
  {
    procedureColumnComparator = getComparator(alphabeticalSort);
  }

  /**
   * Sets whether table columns should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForTableColumns(final boolean alphabeticalSort)
  {
    tableColumnComparator = getComparator(alphabeticalSort);
  }

  /**
   * Sets the column inclusion rule.
   * 
   * @param columnInclusionRule
   *        Column inclusion rule
   */
  public void setColumnInclusionRule(final InclusionRule columnInclusionRule)
  {
    if (columnInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.columnInclusionRule = columnInclusionRule;
  }

  /**
   * Sets the information schema views.
   * 
   * @param informationSchemaViews
   *        Information schema views.
   */
  public void setInformationSchemaViews(final InformationSchemaViews informationSchemaViews)
  {
    if (informationSchemaViews == null)
    {
      this.informationSchemaViews = new InformationSchemaViews();
    }
    else
    {
      this.informationSchemaViews = informationSchemaViews;
    }
  }

  /**
   * Sets the schema information level, identifying to what level the
   * schema should be crawled.
   * 
   * @param schemaInfoLevel
   *        Schema information level.
   */
  public void setSchemaInfoLevel(final SchemaInfoLevel schemaInfoLevel)
  {
    this.schemaInfoLevel = schemaInfoLevel;
  }

  /**
   * Sets the schema pattern.
   * 
   * @param schemaPattern
   *        A schema name pattern; must match the schema name as it is
   *        stored in the database; "" retrieves those without a schema;
   *        <code>null</code> means that the schema name should not be
   *        used to narrow the search.
   */
  public void setSchemaPattern(final String schemaPattern)
  {
    this.schemaPattern = schemaPattern;
  }

  /**
   * Set show stored procedures.
   * 
   * @param showStoredProcedures
   *        Show stored procedures
   */
  public void setShowStoredProcedures(final boolean showStoredProcedures)
  {
    this.showStoredProcedures = showStoredProcedures;
  }

  /**
   * Sets the table inclusion rule.
   * 
   * @param tableInclusionRule
   *        Table inclusion rule
   */
  public void setTableInclusionRule(final InclusionRule tableInclusionRule)
  {
    if (tableInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.tableInclusionRule = tableInclusionRule;
  }

  /**
   * Sets table types from a comma-separated list of table types. For
   * example:
   * TABLE,VIEW,SYSTEM_TABLE,GLOBAL_TEMPORARY,LOCAL_TEMPORARY,ALIAS,SYNONYM
   * 
   * @param tableTypesString
   *        Comma-separated list of table types.
   */
  public void setTableTypes(final String tableTypesString)
  {
    if (tableTypesString == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    tableTypes = TableType.valueOf(tableTypesString.split(","));
  }

  /**
   * Sets table types from an array of table types.
   * 
   * @param tableTypesArray
   *        Array of table types.
   */
  public void setTableTypes(final TableType[] tableTypesArray)
  {
    if (tableTypesArray == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    tableTypes = copyTableTypes(tableTypesArray);
  }

  /**
   * Sets table types from a comma-separated list of table types. For
   * example:
   * TABLE,VIEW,SYSTEM_TABLE,GLOBAL_TEMPORARY,LOCAL_TEMPORARY,ALIAS,SYNONYM
   * 
   * @param tableTypesString
   *        Comma-separated list of table types.
   */
  public void setTableTypesString(final String tableTypesString)
  {
    setTableTypes(tableTypesString);
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    final StringBuffer buffer = new StringBuffer();
    buffer.append("SchemaCrawlerOptions[");
    buffer.append("tableInclusionRule=").append(tableInclusionRule);
    buffer.append(", columnInclusionRule=").append(columnInclusionRule);
    buffer.append(", showStoredProcedures=").append(showStoredProcedures);
    if (tableTypes == null)
    {
      buffer.append(", tableTypes=").append("null");
    }
    else
    {
      buffer.append(", tableTypes=").append(Arrays.asList(tableTypes)
        .toString());
    }
    buffer.append("]");
    return buffer.toString();
  }

  NamedObjectSort getProcedureColumnComparator()
  {
    return procedureColumnComparator;
  }

  NamedObjectSort getTableColumnComparator()
  {
    return tableColumnComparator;
  }

  NamedObjectSort getTableForeignKeyComparator()
  {
    return tableForeignKeyComparator;
  }

  NamedObjectSort getTableIndexComparator()
  {
    return tableIndexComparator;
  }

  private NamedObjectSort getComparator(final boolean alphabeticalSort)
  {
    if (alphabeticalSort)
    {
      return NamedObjectSort.alphabetical;
    }
    else
    {
      return NamedObjectSort.natural;
    }
  }

  private NamedObjectSort getComparator(final String propertyName,
                                        final Config config)
  {
    return getComparator(config.getBooleanValue(propertyName));
  }

}
