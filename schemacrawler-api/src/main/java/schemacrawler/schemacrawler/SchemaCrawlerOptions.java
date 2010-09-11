/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler.schemacrawler;


import schemacrawler.schema.TableType;

/**
 * SchemaCrawler options.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerOptions
  implements Options
{

  public static final String DEFAULT_TABLE_TYPES = "TABLE,VIEW";

  private static final long serialVersionUID = -3557794862382066029L;

  private static final String SC_SCHEMA_PATTERN_EXCLUDE = "schemacrawler.schema.pattern.exclude";
  private static final String SC_SCHEMA_PATTERN_INCLUDE = "schemacrawler.schema.pattern.include";

  private static final String SC_COLUMN_PATTERN_EXCLUDE = "schemacrawler.column.pattern.exclude";
  private static final String SC_COLUMN_PATTERN_INCLUDE = "schemacrawler.column.pattern.include";
  private static final String SC_TABLE_PATTERN_EXCLUDE = "schemacrawler.table.pattern.exclude";
  private static final String SC_TABLE_PATTERN_INCLUDE = "schemacrawler.table.pattern.include";

  private static final String SC_PROCEDURE_COLUMN_PATTERN_EXCLUDE = "schemacrawler.procedure.inout.pattern.exclude";
  private static final String SC_PROCEDURE_COLUMN_PATTERN_INCLUDE = "schemacrawler.procedure.inout.pattern.include";
  private static final String SC_PROCEDURE_PATTERN_EXCLUDE = "schemacrawler.procedure.pattern.exclude";
  private static final String SC_PROCEDURE_PATTERN_INCLUDE = "schemacrawler.procedure.pattern.include";

  private static final String SC_GREP_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.column.pattern.exclude";
  private static final String SC_GREP_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.column.pattern.include";
  private static final String SC_GREP_PROCEDURE_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.procedure.inout.pattern.exclude";
  private static final String SC_GREP_PROCEDURE_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.procedure.inout.pattern.include";
  private static final String SC_GREP_DEFINITION_PATTERN_EXCLUDE = "schemacrawler.grep.definition.pattern.exclude";
  private static final String SC_GREP_DEFINITION_PATTERN_INCLUDE = "schemacrawler.grep.definition.pattern.include";

  private static final String SC_GREP_INVERT_MATCH = "schemacrawler.grep.invert-match";

  private static final String SC_SORT_ALPHABETICALLY_TABLES = "schemacrawler.sort_alphabetically.tables";
  private static final String SC_SORT_ALPHABETICALLY_PROCEDURE_COLUMNS = "schemacrawler.sort_alphabetically.procedure_columns";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_INDEXES = "schemacrawler.sort_alphabetically.table_indices";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS = "schemacrawler.sort_alphabetically.table_foreignkeys";
  private static final String SC_SORT_ALPHABETICALLY_TABLE_COLUMNS = "schemacrawler.sort_alphabetically.table_columns";

  private static TableType[] copyTableTypes(final TableType[] tableTypes)
  {
    final TableType[] tableTypesCopy = new TableType[tableTypes.length];
    System.arraycopy(tableTypes, 0, tableTypesCopy, 0, tableTypes.length);
    return tableTypesCopy;
  }

  private InclusionRule schemaInclusionRule;
  private TableType[] tableTypes;

  private String tableNamePattern;
  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;

  private InclusionRule procedureInclusionRule;
  private InclusionRule procedureColumnInclusionRule;

  private InclusionRule grepColumnInclusionRule;
  private InclusionRule grepProcedureColumnInclusionRule;
  private InclusionRule grepDefinitionInclusionRule;

  private boolean grepInvertMatch;
  private boolean isAlphabeticalSortForTables;
  private boolean isAlphabeticalSortForTableColumns;

  private boolean isAlphabeticalSortForForeignKeys;
  private boolean isAlphabeticalSortForIndexes;
  private boolean isAlphabeticalSortForProcedureColumns;
  private SchemaInfoLevel schemaInfoLevel;
  private InformationSchemaViews informationSchemaViews;

  /**
   * Default options.
   */
  public SchemaCrawlerOptions()
  {
    informationSchemaViews = new InformationSchemaViews();

    schemaInclusionRule = InclusionRule.INCLUDE_ALL;

    tableTypes = new TableType[] {
        TableType.table, TableType.view
    };
    tableInclusionRule = InclusionRule.INCLUDE_ALL;
    columnInclusionRule = InclusionRule.INCLUDE_ALL;

    procedureInclusionRule = InclusionRule.INCLUDE_ALL;
    procedureColumnInclusionRule = InclusionRule.INCLUDE_ALL;

    isAlphabeticalSortForTables = true;
    isAlphabeticalSortForTableColumns = false;
    isAlphabeticalSortForForeignKeys = false;
    isAlphabeticalSortForIndexes = false;
    isAlphabeticalSortForProcedureColumns = false;
  }

  /**
   * Options from properties.
   * 
   * @param config
   *        Configuration properties
   */
  public SchemaCrawlerOptions(final Config config)
  {
    this();
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = config;
    }

    informationSchemaViews = new InformationSchemaViews(config);

    schemaInclusionRule = new InclusionRule(configProperties
                                              .getStringValue(SC_SCHEMA_PATTERN_INCLUDE,
                                                              InclusionRule.ALL),
                                            configProperties
                                              .getStringValue(SC_SCHEMA_PATTERN_EXCLUDE,
                                                              InclusionRule.NONE));

    tableInclusionRule = new InclusionRule(configProperties
                                             .getStringValue(SC_TABLE_PATTERN_INCLUDE,
                                                             InclusionRule.ALL),
                                           configProperties
                                             .getStringValue(SC_TABLE_PATTERN_EXCLUDE,
                                                             InclusionRule.NONE));
    columnInclusionRule = new InclusionRule(configProperties
                                              .getStringValue(SC_COLUMN_PATTERN_INCLUDE,
                                                              InclusionRule.ALL),
                                            configProperties
                                              .getStringValue(SC_COLUMN_PATTERN_EXCLUDE,
                                                              InclusionRule.NONE));

    procedureInclusionRule = new InclusionRule(configProperties
                                                 .getStringValue(SC_PROCEDURE_PATTERN_INCLUDE,
                                                                 InclusionRule.ALL),
                                               configProperties
                                                 .getStringValue(SC_PROCEDURE_PATTERN_EXCLUDE,
                                                                 InclusionRule.NONE));
    procedureColumnInclusionRule = new InclusionRule(configProperties
                                                       .getStringValue(SC_PROCEDURE_COLUMN_PATTERN_INCLUDE,
                                                                       InclusionRule.ALL),
                                                     configProperties
                                                       .getStringValue(SC_PROCEDURE_COLUMN_PATTERN_EXCLUDE,
                                                                       InclusionRule.NONE));

    grepColumnInclusionRule = new InclusionRule(configProperties
                                                  .getStringValue(SC_GREP_COLUMN_PATTERN_INCLUDE,
                                                                  InclusionRule.ALL),
                                                configProperties
                                                  .getStringValue(SC_GREP_COLUMN_PATTERN_EXCLUDE,
                                                                  InclusionRule.NONE));
    grepProcedureColumnInclusionRule = new InclusionRule(configProperties
      .getStringValue(SC_GREP_PROCEDURE_COLUMN_PATTERN_INCLUDE,
                      InclusionRule.ALL), configProperties
      .getStringValue(SC_GREP_PROCEDURE_COLUMN_PATTERN_EXCLUDE,
                      InclusionRule.NONE));
    grepDefinitionInclusionRule = new InclusionRule(configProperties
                                                      .getStringValue(SC_GREP_DEFINITION_PATTERN_INCLUDE,
                                                                      InclusionRule.ALL),
                                                    configProperties
                                                      .getStringValue(SC_GREP_DEFINITION_PATTERN_EXCLUDE,
                                                                      InclusionRule.NONE));
    grepInvertMatch = configProperties.getBooleanValue(SC_GREP_INVERT_MATCH);

    isAlphabeticalSortForTables = Boolean.parseBoolean(configProperties
      .getStringValue(SC_SORT_ALPHABETICALLY_TABLES, "true"));
    isAlphabeticalSortForTableColumns = configProperties
      .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_COLUMNS);
    isAlphabeticalSortForForeignKeys = configProperties
      .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS);
    isAlphabeticalSortForIndexes = configProperties
      .getBooleanValue(SC_SORT_ALPHABETICALLY_TABLE_INDEXES);
    isAlphabeticalSortForProcedureColumns = configProperties
      .getBooleanValue(SC_SORT_ALPHABETICALLY_PROCEDURE_COLUMNS);

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
   * Gets the column inclusion rule for grep.
   * 
   * @return Column inclusion rule for grep.
   */
  public InclusionRule getGrepColumnInclusionRule()
  {
    return grepColumnInclusionRule;
  }

  /**
   * Gets the definitions inclusion rule for grep.
   * 
   * @return Definitions inclusion rule for grep.
   */
  public InclusionRule getGrepDefinitionInclusionRule()
  {
    return grepDefinitionInclusionRule;
  }

  /**
   * Gets the procedure column rule for grep.
   * 
   * @return Procedure column rule for grep.
   */
  public InclusionRule getGrepProcedureColumnInclusionRule()
  {
    return grepProcedureColumnInclusionRule;
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
   * Gets the procedure column rule.
   * 
   * @return Procedure column rule.
   */
  public InclusionRule getProcedureColumnInclusionRule()
  {
    return procedureColumnInclusionRule;
  }

  /**
   * Gets the procedure inclusion rule.
   * 
   * @return Procedure inclusion rule.
   */
  public InclusionRule getProcedureInclusionRule()
  {
    return procedureInclusionRule;
  }

  /**
   * Gets the schema inclusion rule.
   * 
   * @return Schema inclusion rule.
   */
  public InclusionRule getSchemaInclusionRule()
  {
    return schemaInclusionRule;
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
      return SchemaInfoLevel.standard();
    }
    else
    {
      return schemaInfoLevel;
    }
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
   * Gets the table name pattern.
   * 
   * @return Table name pattern
   */
  public String getTableNamePattern()
  {
    return tableNamePattern;
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
    return isAlphabeticalSortForForeignKeys;
  }

  /**
   * Whether indexes are alphabetically sorted.
   * 
   * @return Whether indexes are alphabetically sorted
   */
  public boolean isAlphabeticalSortForIndexes()
  {
    return isAlphabeticalSortForIndexes;
  }

  /**
   * Whether procedure columns are alphabetically sorted.
   * 
   * @return Whether procedure columns are alphabetically sorted
   */
  public boolean isAlphabeticalSortForProcedureColumns()
  {
    return isAlphabeticalSortForProcedureColumns;
  }

  /**
   * Whether table columns are alphabetically sorted.
   * 
   * @return Whether table columns are alphabetically sorted
   */
  public boolean isAlphabeticalSortForTableColumns()
  {
    return isAlphabeticalSortForTableColumns;
  }

  /**
   * Whether tables are alphabetically sorted.
   * 
   * @return Whether tables are alphabetically sorted
   */
  public boolean isAlphabeticalSortForTables()
  {
    return isAlphabeticalSortForTables;
  }

  /**
   * Whether to invert matches.
   * 
   * @return Whether to invert matches.
   */
  public boolean isGrepInvertMatch()
  {
    return grepInvertMatch;
  }

  /**
   * Sets whether foreign keys should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForForeignKeys(final boolean alphabeticalSort)
  {
    isAlphabeticalSortForForeignKeys = alphabeticalSort;
  }

  /**
   * Sets whether indexes should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForIndexes(final boolean alphabeticalSort)
  {
    isAlphabeticalSortForIndexes = alphabeticalSort;
  }

  /**
   * Sets whether procedure columns should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForProcedureColumns(final boolean alphabeticalSort)
  {
    isAlphabeticalSortForProcedureColumns = alphabeticalSort;
  }

  /**
   * Sets whether table columns should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForTableColumns(final boolean alphabeticalSort)
  {
    isAlphabeticalSortForTableColumns = alphabeticalSort;
  }

  /**
   * Sets whether tables should be alphabetically sorted.
   * 
   * @param alphabeticalSort
   *        Alphabetical sort
   */
  public void setAlphabeticalSortForTables(final boolean alphabeticalSort)
  {
    isAlphabeticalSortForTables = alphabeticalSort;
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
   * Sets the column inclusion rule for grep.
   * 
   * @param grepColumnInclusionRule
   *        Column inclusion rule for grep
   */
  public void setGrepColumnInclusionRule(final InclusionRule grepColumnInclusionRule)
  {
    this.grepColumnInclusionRule = grepColumnInclusionRule;
  }

  /**
   * Sets the definition inclusion rule for grep.
   * 
   * @param grepDefinitionInclusionRule
   *        Definition inclusion rule for grep
   */
  public void setGrepDefinitionInclusionRule(InclusionRule grepDefinitionInclusionRule)
  {
    this.grepDefinitionInclusionRule = grepDefinitionInclusionRule;
  }

  /**
   * Set whether to invert matches.
   * 
   * @param grepInvertMatch
   *        Whether to invert matches.
   */
  public void setGrepInvertMatch(final boolean grepInvertMatch)
  {
    this.grepInvertMatch = grepInvertMatch;
  }

  /**
   * Sets the procedure column inclusion rule for grep.
   * 
   * @param grepProcedureColumnInclusionRule
   *        Procedure column inclusion rule for grep
   */
  public void setGrepProcedureColumnInclusionRule(final InclusionRule grepProcedureColumnInclusionRule)
  {
    this.grepProcedureColumnInclusionRule = grepProcedureColumnInclusionRule;
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
   * Sets the procedure column inclusion rule.
   * 
   * @param procedureColumnInclusionRule
   *        Procedure column inclusion rule
   */
  public void setProcedureColumnInclusionRule(final InclusionRule procedureColumnInclusionRule)
  {
    if (procedureColumnInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.procedureColumnInclusionRule = procedureColumnInclusionRule;
  }

  /**
   * Sets the procedure inclusion rule.
   * 
   * @param procedureInclusionRule
   *        Procedure inclusion rule
   */
  public void setProcedureInclusionRule(final InclusionRule procedureInclusionRule)
  {
    if (procedureInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.procedureInclusionRule = procedureInclusionRule;
  }

  /**
   * Sets the schema inclusion rule.
   * 
   * @param schemaInclusionRule
   *        Schema inclusion rule
   */
  public void setSchemaInclusionRule(final InclusionRule schemaInclusionRule)
  {
    if (schemaInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.schemaInclusionRule = schemaInclusionRule;
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
   * Sets the table name pattern, using the JDBC syntax for wildcards (_
   * and *). The table name pattern is case-sensitive, and matches just
   * the table name - not the fully qualified table name. The table name
   * pattern restricts the tables retrieved at an early stage in the
   * retrieval process, so it must be used only when performance needs
   * to be tuned.
   * 
   * @param tableNamePattern
   *        Table name pattern
   */
  public void setTableNamePattern(final String tableNamePattern)
  {
    this.tableNamePattern = tableNamePattern;
  }

  /**
   * Sets table types from a comma-separated list of table types. For
   * example:
   * TABLE,VIEW,SYSTEM_TABLE,GLOBAL_TEMPORARY,LOCAL_TEMPORARY,ALIAS
   * ,SYNONYM
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
   * TABLE,VIEW,SYSTEM_TABLE,GLOBAL_TEMPORARY,LOCAL_TEMPORARY,ALIAS
   * ,SYNONYM
   * 
   * @param tableTypesString
   *        Comma-separated list of table types.
   */
  public void setTableTypesString(final String tableTypesString)
  {
    setTableTypes(tableTypesString);
  }

}
