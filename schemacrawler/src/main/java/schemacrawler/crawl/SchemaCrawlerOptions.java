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


import java.util.Arrays;
import java.util.Properties;
import java.util.regex.Pattern;

import schemacrawler.BaseOptions;
import schemacrawler.schema.TableType;
import schemacrawler.util.AlphabeticalSortComparator;
import schemacrawler.util.NaturalSortComparator;
import schemacrawler.util.SerializableComparator;

/**
 * SchemaCrarlwe options.
 * 
 * @author sfatehi
 */
public final class SchemaCrawlerOptions
  extends BaseOptions
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

  private final TableType[] tableTypes;
  private boolean showStoredProcedures;

  private final InclusionRule tableInclusionRule;
  private final InclusionRule columnInclusionRule;

  private final SerializableComparator tableColumnComparator;
  private final SerializableComparator tableForeignKeyComparator;
  private final SerializableComparator tableIndexComparator;
  private final SerializableComparator procedureColumnComparator;

  /**
   * Default options.
   */
  public SchemaCrawlerOptions()
  {
    tableTypes = TableType.valueOf(DEFAULT_TABLE_TYPES.split(","));

    showStoredProcedures = false;

    tableInclusionRule = new InclusionRule();
    columnInclusionRule = new InclusionRule();

    tableColumnComparator = new NaturalSortComparator();
    tableForeignKeyComparator = new NaturalSortComparator();
    tableIndexComparator = new NaturalSortComparator();
    procedureColumnComparator = new NaturalSortComparator();

  }

  /**
   * Options from properties.
   * 
   * @param tableTypes
   *        Table types to show
   * @param tableInclusionRule
   *        Rule for including or excluding columns
   * @param columnInclusionRule
   *        Rule for including or excluding columns
   * @param config
   *        Configuration properties
   */
  public SchemaCrawlerOptions(final Properties config,
      final TableType[] tableTypes, final InclusionRule tableInclusionRule,
      final InclusionRule columnInclusionRule)
  {

    if (tableTypes == null)
    {
      this.tableTypes = TableType.valueOf(DEFAULT_TABLE_TYPES.split(","));
    } else
    {
      final int size = tableTypes.length;
      this.tableTypes = new TableType[size];
      System.arraycopy(tableTypes, 0, this.tableTypes, 0, size);
    }

    this.tableInclusionRule = tableInclusionRule;
    this.columnInclusionRule = columnInclusionRule;

    showStoredProcedures = getBooleanProperty(SC_SHOW_STORED_PROCEDURES, config);

    // comparators
    tableColumnComparator = getComparator(SC_SORT_ALPHABETICALLY_TABLE_COLUMNS,
        config);
    tableForeignKeyComparator = getComparator(
        SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS, config);
    tableIndexComparator = getComparator(SC_SORT_ALPHABETICALLY_TABLE_INDICES,
        config);
    procedureColumnComparator = getComparator(
        SC_SORT_ALPHABETICALLY_PROCEDURE_COLUMNS, config);
  }

  /**
   * Options from properties.
   * 
   * @param config
   *        Configuration properties
   */
  public SchemaCrawlerOptions(final Properties config)
  {

    final String tableTypesString = config.getProperty(SC_TABLE_TYPES,
        DEFAULT_TABLE_TYPES);
    tableTypes = TableType.valueOf(tableTypesString.split(","));

    showStoredProcedures = getBooleanProperty(SC_SHOW_STORED_PROCEDURES, config);

    tableInclusionRule = new InclusionRule(Pattern.compile(config.getProperty(
        SC_TABLE_PATTERN_INCLUDE, ".*")), Pattern.compile(config.getProperty(
        SC_TABLE_PATTERN_EXCLUDE, ".*")));
    columnInclusionRule = new InclusionRule(Pattern.compile(config.getProperty(
        SC_COLUMN_PATTERN_INCLUDE, ".*")), Pattern.compile(config.getProperty(
        SC_COLUMN_PATTERN_EXCLUDE, ".*")));

    // comparators
    tableColumnComparator = getComparator(SC_SORT_ALPHABETICALLY_TABLE_COLUMNS,
        config);
    tableForeignKeyComparator = getComparator(
        SC_SORT_ALPHABETICALLY_TABLE_FOREIGNKEYS, config);
    tableIndexComparator = getComparator(SC_SORT_ALPHABETICALLY_TABLE_INDICES,
        config);
    procedureColumnComparator = getComparator(
        SC_SORT_ALPHABETICALLY_PROCEDURE_COLUMNS, config);
  }

  private SerializableComparator getComparator(final String propertyName,
      final Properties config)
  {
    if (getBooleanProperty(propertyName, config))
    {
      return new AlphabeticalSortComparator();
    } else
    {
      return new NaturalSortComparator();
    }
  }

  InclusionRule getColumnInclusionRule()
  {
    return columnInclusionRule;
  }

  InclusionRule getTableInclusionRule()
  {
    return tableInclusionRule;
  }

  boolean isShowStoredProcedures()
  {
    return showStoredProcedures;
  }

  /**
   * Get the table types.
   * 
   * @return Table types
   */
  TableType[] getTableTypes()
  {
    final TableType[] tableTypesCopy = new TableType[tableTypes.length];
    System.arraycopy(tableTypes, 0, tableTypesCopy, 0, tableTypes.length);
    return tableTypesCopy;
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
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
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
    } else
    {
      buffer.append(", tableTypes=").append(
          Arrays.asList(tableTypes).toString());
    }
    buffer.append("]");
    return buffer.toString();
  }

  SerializableComparator getTableColumnComparator()
  {
    return tableColumnComparator;
  }

  SerializableComparator getTableForeignKeyComparator()
  {
    return tableForeignKeyComparator;
  }

  SerializableComparator getTableIndexComparator()
  {
    return tableIndexComparator;
  }

  SerializableComparator getProcedureColumnComparator()
  {
    return procedureColumnComparator;
  }

}
