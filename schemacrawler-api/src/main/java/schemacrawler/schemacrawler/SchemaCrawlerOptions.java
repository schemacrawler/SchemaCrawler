/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;

import schemacrawler.schema.RoutineType;
import schemacrawler.schema.TableType;

/**
 * SchemaCrawler options.
 * 
 * @author Sualeh Fatehi
 */
public final class SchemaCrawlerOptions
  implements Options
{

  private static final long serialVersionUID = -3557794862382066029L;

  private static final String SC_SCHEMA_PATTERN_EXCLUDE = "schemacrawler.schema.pattern.exclude";
  private static final String SC_SCHEMA_PATTERN_INCLUDE = "schemacrawler.schema.pattern.include";

  private static final String SC_COLUMN_PATTERN_EXCLUDE = "schemacrawler.column.pattern.exclude";
  private static final String SC_COLUMN_PATTERN_INCLUDE = "schemacrawler.column.pattern.include";
  private static final String SC_TABLE_PATTERN_EXCLUDE = "schemacrawler.table.pattern.exclude";
  private static final String SC_TABLE_PATTERN_INCLUDE = "schemacrawler.table.pattern.include";

  private static final String SC_ROUTINE_COLUMN_PATTERN_EXCLUDE = "schemacrawler.routine.inout.pattern.exclude";
  private static final String SC_ROUTINE_COLUMN_PATTERN_INCLUDE = "schemacrawler.routine.inout.pattern.include";
  private static final String SC_ROUTINE_PATTERN_EXCLUDE = "schemacrawler.routine.pattern.exclude";
  private static final String SC_ROUTINE_PATTERN_INCLUDE = "schemacrawler.routine.pattern.include";

  private static final String SC_GREP_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.column.pattern.exclude";
  private static final String SC_GREP_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.column.pattern.include";
  private static final String SC_GREP_ROUTINE_COLUMN_PATTERN_EXCLUDE = "schemacrawler.grep.routine.inout.pattern.exclude";
  private static final String SC_GREP_ROUTINE_COLUMN_PATTERN_INCLUDE = "schemacrawler.grep.routine.inout.pattern.include";
  private static final String SC_GREP_DEFINITION_PATTERN_EXCLUDE = "schemacrawler.grep.definition.pattern.exclude";
  private static final String SC_GREP_DEFINITION_PATTERN_INCLUDE = "schemacrawler.grep.definition.pattern.include";

  private static final String SC_GREP_INVERT_MATCH = "schemacrawler.grep.invert-match";

  private static final String SC_SUPPORTS_SCHEMAS_OVERRIDE = "schemacrawler.supports_schemas.override";
  private static final String SC_SUPPORTS_CATALOG_OVERRIDE = "schemacrawler.supports_catalog.override";

  private InclusionRule schemaInclusionRule;

  private Collection<TableType> tableTypes;
  private String tableNamePattern;
  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;

  private Collection<RoutineType> routineTypes;
  private InclusionRule routineInclusionRule;
  private InclusionRule routineColumnInclusionRule;

  private InclusionRule synonymInclusionRule;
  private InclusionRule grepColumnInclusionRule;
  private InclusionRule grepRoutineColumnInclusionRule;
  private InclusionRule grepDefinitionInclusionRule;
  private boolean grepInvertMatch;
  private int childTableFilterDepth;
  private int parentTableFilterDepth;

  private SchemaInfoLevel schemaInfoLevel;
  private InformationSchemaViews informationSchemaViews;

  private boolean hasOverrideForSupportsSchemas;
  private boolean isSupportsSchemasOverride;
  private boolean hasOverrideForSupportsCatalogs;

  private boolean isSupportsCatalogOverride;

  /**
   * Default options.
   */
  public SchemaCrawlerOptions()
  {
    informationSchemaViews = new InformationSchemaViews();

    schemaInclusionRule = InclusionRule.INCLUDE_ALL;

    tableTypes = new HashSet<TableType>(Arrays.asList(TableType.table,
                                                      TableType.view));
    tableInclusionRule = InclusionRule.INCLUDE_ALL;
    columnInclusionRule = InclusionRule.INCLUDE_ALL;

    routineTypes = new HashSet<RoutineType>(Arrays.asList(RoutineType.procedure,
                                                          RoutineType.function));
    routineInclusionRule = InclusionRule.INCLUDE_ALL;
    routineColumnInclusionRule = InclusionRule.INCLUDE_ALL;

    synonymInclusionRule = InclusionRule.INCLUDE_ALL;

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

    schemaInclusionRule = new InclusionRule(configProperties.getStringValue(SC_SCHEMA_PATTERN_INCLUDE,
                                                                            InclusionRule.ALL),
                                            configProperties
                                              .getStringValue(SC_SCHEMA_PATTERN_EXCLUDE,
                                                              InclusionRule.NONE));

    tableInclusionRule = new InclusionRule(configProperties.getStringValue(SC_TABLE_PATTERN_INCLUDE,
                                                                           InclusionRule.ALL),
                                           configProperties
                                             .getStringValue(SC_TABLE_PATTERN_EXCLUDE,
                                                             InclusionRule.NONE));
    columnInclusionRule = new InclusionRule(configProperties.getStringValue(SC_COLUMN_PATTERN_INCLUDE,
                                                                            InclusionRule.ALL),
                                            configProperties
                                              .getStringValue(SC_COLUMN_PATTERN_EXCLUDE,
                                                              InclusionRule.NONE));

    routineInclusionRule = new InclusionRule(configProperties.getStringValue(SC_ROUTINE_PATTERN_INCLUDE,
                                                                             InclusionRule.ALL),
                                             configProperties
                                               .getStringValue(SC_ROUTINE_PATTERN_EXCLUDE,
                                                               InclusionRule.NONE));
    routineColumnInclusionRule = new InclusionRule(configProperties
                                                     .getStringValue(SC_ROUTINE_COLUMN_PATTERN_INCLUDE,
                                                                     InclusionRule.ALL),
                                                   configProperties
                                                     .getStringValue(SC_ROUTINE_COLUMN_PATTERN_EXCLUDE,
                                                                     InclusionRule.NONE));

    grepColumnInclusionRule = new InclusionRule(configProperties
                                                  .getStringValue(SC_GREP_COLUMN_PATTERN_INCLUDE,
                                                                  InclusionRule.ALL),
                                                configProperties
                                                  .getStringValue(SC_GREP_COLUMN_PATTERN_EXCLUDE,
                                                                  InclusionRule.NONE));
    grepRoutineColumnInclusionRule = new InclusionRule(configProperties
                                                         .getStringValue(SC_GREP_ROUTINE_COLUMN_PATTERN_INCLUDE,
                                                                         InclusionRule.ALL),
                                                       configProperties
                                                         .getStringValue(SC_GREP_ROUTINE_COLUMN_PATTERN_EXCLUDE,
                                                                         InclusionRule.NONE));
    grepDefinitionInclusionRule = new InclusionRule(configProperties
                                                      .getStringValue(SC_GREP_DEFINITION_PATTERN_INCLUDE,
                                                                      InclusionRule.ALL),
                                                    configProperties
                                                      .getStringValue(SC_GREP_DEFINITION_PATTERN_EXCLUDE,
                                                                      InclusionRule.NONE));
    grepInvertMatch = configProperties.getBooleanValue(SC_GREP_INVERT_MATCH);

    if (configProperties.hasValue(SC_SUPPORTS_SCHEMAS_OVERRIDE))
    {
      setSupportsSchemasOverride(configProperties
        .getBooleanValue(SC_SUPPORTS_SCHEMAS_OVERRIDE));
    }
    if (configProperties.hasValue(SC_SUPPORTS_CATALOG_OVERRIDE))
    {
      setSupportsCatalogOverride(configProperties
        .getBooleanValue(SC_SUPPORTS_CATALOG_OVERRIDE));
    }

  }

  public int getChildTableFilterDepth()
  {
    return childTableFilterDepth;
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
   * Gets the routine column rule for grep.
   * 
   * @return Routine column rule for grep.
   */
  public InclusionRule getGrepRoutineColumnInclusionRule()
  {
    return grepRoutineColumnInclusionRule;
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

  public int getParentTableFilterDepth()
  {
    return parentTableFilterDepth;
  }

  /**
   * Gets the routine column rule.
   * 
   * @return Routine column rule.
   */
  public InclusionRule getRoutineColumnInclusionRule()
  {
    return routineColumnInclusionRule;
  }

  /**
   * Gets the routine inclusion rule.
   * 
   * @return Routine inclusion rule.
   */
  public InclusionRule getRoutineInclusionRule()
  {
    return routineInclusionRule;
  }

  public Collection<RoutineType> getRoutineTypes()
  {
    return new HashSet<RoutineType>(routineTypes);
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
   * Gets the synonym inclusion rule.
   * 
   * @return Synonym inclusion rule.
   */
  public InclusionRule getSynonymInclusionRule()
  {
    return synonymInclusionRule;
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

  public Collection<TableType> getTableTypes()
  {
    return new HashSet<TableType>(tableTypes);
  }

  public boolean hasOverrideForSupportsCatalogs()
  {
    return hasOverrideForSupportsCatalogs;
  }

  public boolean hasOverrideForSupportsSchemas()
  {
    return hasOverrideForSupportsSchemas;
  }

  public boolean isGrepColumns()
  {
    return grepColumnInclusionRule != null;
  }

  public boolean isGrepDefinitions()
  {
    return grepDefinitionInclusionRule != null;
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

  public boolean isGrepRoutineColumns()
  {
    return grepRoutineColumnInclusionRule != null;
  }

  public boolean isSupportsCatalogOverride()
  {
    return isSupportsCatalogOverride;
  }

  public boolean isSupportsSchemasOverride()
  {
    return isSupportsSchemasOverride;
  }

  public void setChildTableFilterDepth(final int childTableFilterDepth)
  {
    this.childTableFilterDepth = childTableFilterDepth;
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
  public void setGrepDefinitionInclusionRule(final InclusionRule grepDefinitionInclusionRule)
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
   * Sets the routine column inclusion rule for grep.
   * 
   * @param grepRoutineColumnInclusionRule
   *        Routine column inclusion rule for grep
   */
  public void setGrepRoutineColumnInclusionRule(final InclusionRule grepRoutineColumnInclusionRule)
  {
    this.grepRoutineColumnInclusionRule = grepRoutineColumnInclusionRule;
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

  public void setParentTableFilterDepth(final int parentTableFilterDepth)
  {
    this.parentTableFilterDepth = parentTableFilterDepth;
  }

  /**
   * Sets the routine column inclusion rule.
   * 
   * @param routineColumnInclusionRule
   *        Routine column inclusion rule
   */
  public void setRoutineColumnInclusionRule(final InclusionRule routineColumnInclusionRule)
  {
    if (routineColumnInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.routineColumnInclusionRule = routineColumnInclusionRule;
  }

  /**
   * Sets the routine inclusion rule.
   * 
   * @param routineInclusionRule
   *        Routine inclusion rule
   */
  public void setRoutineInclusionRule(final InclusionRule routineInclusionRule)
  {
    if (routineInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.routineInclusionRule = routineInclusionRule;
  }

  public void setRoutineTypes(final Collection<RoutineType> routineTypes)
  {
    if (routineTypes == null)
    {
      this.routineTypes = Collections.emptySet();
    }
    else
    {
      this.routineTypes = new HashSet<RoutineType>(routineTypes);
    }
  }

  /**
   * Sets routine types from a comma-separated list of routine types.
   * 
   * @param routineTypesString
   *        Comma-separated list of routine types.
   */
  public void setRoutineTypes(final String routineTypesString)
  {
    routineTypes = new HashSet<RoutineType>();
    if (routineTypesString != null)
    {
      final String[] routineTypeStrings = routineTypesString.split(",");
      if (routineTypeStrings != null && routineTypeStrings.length > 0)
      {
        for (final String routineTypeString: routineTypeStrings)
        {
          routineTypes.add(RoutineType.valueOf(routineTypeString
            .toLowerCase(Locale.ENGLISH)));
        }
      }
    }
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
   * Overrides the JDBC driver provided information about whether the
   * database supports catalogs. Cannot be unset.
   * 
   * @param isSupportsCatalogOverride
   *        Value for the override
   */
  public void setSupportsCatalogOverride(final boolean isSupportsCatalogOverride)
  {
    if (hasOverrideForSupportsCatalogs)
    {
      throw new IllegalAccessError("Cannot reset or unset override for catalog support");
    }

    hasOverrideForSupportsCatalogs = true;
    this.isSupportsCatalogOverride = isSupportsCatalogOverride;
  }

  /**
   * Overrides the JDBC driver provided information about whether the
   * database supports schema. Cannot be unset.
   * 
   * @param isSupportsSchemasOverride
   *        Value for the override
   */
  public void setSupportsSchemasOverride(final boolean isSupportsSchemasOverride)
  {
    if (hasOverrideForSupportsSchemas)
    {
      throw new IllegalAccessError("Cannot reset or unset override for schema support");
    }

    hasOverrideForSupportsSchemas = true;
    this.isSupportsSchemasOverride = isSupportsSchemasOverride;
  }

  /**
   * Sets the synonym inclusion rule.
   * 
   * @param synonymInclusionRule
   *        Synonym inclusion rule
   */
  public void setSynonymInclusionRule(final InclusionRule synonymInclusionRule)
  {
    if (synonymInclusionRule == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.synonymInclusionRule = synonymInclusionRule;
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

  public void setTableTypes(final Collection<TableType> tableTypes)
  {
    if (tableTypes == null)
    {
      this.tableTypes = Collections.emptySet();
    }
    else
    {
      this.tableTypes = new HashSet<TableType>(tableTypes);
    }
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
    tableTypes = new HashSet<TableType>();
    if (tableTypesString != null)
    {
      final String[] tableTypeStrings = tableTypesString.split(",");
      if (tableTypeStrings != null && tableTypeStrings.length > 0)
      {
        for (final String tableTypeString: tableTypeStrings)
        {
          tableTypes.add(TableType.valueOf(tableTypeString
            .toLowerCase(Locale.ENGLISH)));
        }
      }
    }
  }

}
