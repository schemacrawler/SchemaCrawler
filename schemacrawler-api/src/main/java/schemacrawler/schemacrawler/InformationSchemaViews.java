/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.ADDITIONAL_COLUMN_ATTRIBUTES;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.ADDITIONAL_TABLE_ATTRIBUTES;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.CONSTRAINT_COLUMN_USAGE;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.EXT_INDEXES;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.EXT_SYNONYMS;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.EXT_TABLES;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.EXT_TABLE_CONSTRAINTS;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.OVERRIDE_TYPE_INFO;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.ROUTINES;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.SCHEMATA;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.SEQUENCES;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.TABLE_CONSTRAINTS;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.TRIGGERS;
import static schemacrawler.schemacrawler.InformationSchemaViews.InformationSchemaKey.VIEWS;
import static sf.util.Utility.isBlank;
import static sf.util.Utility.readResourceFully;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

import sf.util.ObjectToString;

/**
 * The database specific views to get additional database metadata in a
 * standard format.
 *
 * @author Sualeh Fatehi
 */
public final class InformationSchemaViews
  implements Options
{

  protected enum InformationSchemaKey
  {

    ADDITIONAL_COLUMN_ATTRIBUTES("select.ADDITIONAL_COLUMN_ATTRIBUTES"),
    ADDITIONAL_TABLE_ATTRIBUTES("select.ADDITIONAL_TABLE_ATTRIBUTES"),
    CONSTRAINT_COLUMN_USAGE("select.INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE"),
    EXT_INDEXES("select.INFORMATION_SCHEMA.EXT_INDEXES"),
    EXT_SYNONYMS("select.INFORMATION_SCHEMA.EXT_SYNONYMS"),
    EXT_TABLES("select.INFORMATION_SCHEMA.EXT_TABLES"),
    EXT_TABLE_CONSTRAINTS("select.INFORMATION_SCHEMA.EXT_TABLE_CONSTRAINTS"),
    OVERRIDE_TYPE_INFO("select.OVERRIDE_TYPE_INFO"),
    ROUTINES("select.INFORMATION_SCHEMA.ROUTINES"),
    SCHEMATA("select.INFORMATION_SCHEMA.SCHEMATA"),
    SEQUENCES("select.INFORMATION_SCHEMA.SEQUENCES"),
    TABLE_CONSTRAINTS("select.INFORMATION_SCHEMA.TABLE_CONSTRAINTS"),
    TRIGGERS("select.INFORMATION_SCHEMA.TRIGGERS"),
    VIEWS("select.INFORMATION_SCHEMA.VIEWS");

    private final String lookupKey;

    private InformationSchemaKey(final String lookupKey)
    {
      this.lookupKey = lookupKey;
    }

    public String getLookupKey()
    {
      return lookupKey;
    }

    public String getResource()
    {
      return name() + ".sql";
    }

  }

  private static final long serialVersionUID = 3587581365346059044L;

  private final Map<InformationSchemaKey, String> informationSchemaQueries;

  /**
   * Creates empty information schema views.
   */
  public InformationSchemaViews()
  {
    this(null);
  }

  /**
   * Information schema views from a map.
   *
   * @param informationSchemaViewsSql
   *        Map of information schema view definitions.
   */
  InformationSchemaViews(final Map<String, String> informationSchemaViewsSql)
  {
    informationSchemaQueries = new HashMap<>();
    if (informationSchemaViewsSql != null)
    {
      for (final InformationSchemaKey key: InformationSchemaKey.values())
      {
        if (informationSchemaViewsSql.containsKey(key.getLookupKey()))
        {
          try
          {
            informationSchemaQueries.put(key, informationSchemaViewsSql.get(key
              .getLookupKey()));
          }
          catch (final IllegalArgumentException e)
          {
            // Ignore
          }
        }
      }
    }
  }

  /**
   * Gets the additional attributes SQL for columns, from the additional
   * configuration.
   *
   * @return Additional attributes SQL for columns.
   */
  public String getAdditionalColumnAttributesSql()
  {
    return informationSchemaQueries.get(ADDITIONAL_COLUMN_ATTRIBUTES);
  }

  /**
   * Gets the additional attributes SQL for tables, from the additional
   * configuration.
   *
   * @return Additional attributes SQL for tables.
   */
  public String getAdditionalTableAttributesSql()
  {
    return informationSchemaQueries.get(ADDITIONAL_TABLE_ATTRIBUTES);
  }

  /**
   * Gets the index definitions SQL from the additional configuration.
   *
   * @return Index definitions SQL.
   */
  public String getExtIndexesSql()
  {
    return informationSchemaQueries.get(EXT_INDEXES);
  }

  /**
   * Gets the table check constraints SQL from the additional
   * configuration.
   *
   * @return Table check constraints SQL.
   */
  public String getExtTableConstraintsSql()
  {
    return informationSchemaQueries.get(EXT_TABLE_CONSTRAINTS);
  }

  /**
   * Gets the table definitions SQL from the additional configuration.
   *
   * @return Table definitions SQL.
   */
  public String getExtTablesSql()
  {
    return informationSchemaQueries.get(EXT_TABLES);
  }

  /**
   * SQL that overrides DatabaseMetaData#getTypeInfo().
   * {@link DatabaseMetaData#getTypeInfo()}
   *
   * @return SQL that overrides DatabaseMetaData#getTypeInfo().
   */
  public String getOverrideTypeInfoSql()
  {
    return informationSchemaQueries.get(OVERRIDE_TYPE_INFO);
  }

  /**
   * Gets the routine definitions SQL from the additional configuration.
   *
   * @return Routine definitions SQL.
   */
  public String getRoutinesSql()
  {
    return informationSchemaQueries.get(ROUTINES);
  }

  /**
   * Gets the schemata SQL from the additional configuration.
   *
   * @return Schemata SQL.
   */
  public String getSchemataSql()
  {
    return informationSchemaQueries.get(SCHEMATA);
  }

  /**
   * Gets the sequences SQL from the additional configuration.
   *
   * @return Sequences SQL.
   */
  public String getSequencesSql()
  {
    return informationSchemaQueries.get(SEQUENCES);
  }

  /**
   * Gets the synonyms SQL from the additional configuration.
   *
   * @return Synonyms SQL.
   */
  public String getSynonymsSql()
  {
    return informationSchemaQueries.get(EXT_SYNONYMS);
  }

  /**
   * Gets the table constraints columns SQL from the additional
   * configuration.
   *
   * @return Table constraints columns SQL.
   */
  public String getTableConstraintsColumnsSql()
  {
    return informationSchemaQueries.get(CONSTRAINT_COLUMN_USAGE);
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   *
   * @return Table constraints SQL.
   */
  public String getTableConstraintsSql()
  {
    return informationSchemaQueries.get(TABLE_CONSTRAINTS);
  }

  /**
   * Gets the trigger definitions SQL from the additional configuration.
   *
   * @return Trigger definitions SQL.
   */
  public String getTriggersSql()
  {
    return informationSchemaQueries.get(TRIGGERS);
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   *
   * @return View definitions SQL.
   */
  public String getViewsSql()
  {
    return informationSchemaQueries.get(VIEWS);
  }

  public boolean hasAdditionalColumnAttributesSql()
  {
    return informationSchemaQueries.containsKey(ADDITIONAL_COLUMN_ATTRIBUTES);
  }

  public boolean hasAdditionalTableAttributesSql()
  {
    return informationSchemaQueries.containsKey(ADDITIONAL_TABLE_ATTRIBUTES);
  }

  public boolean hasExtIndexesSql()
  {
    return informationSchemaQueries.containsKey(EXT_INDEXES);
  }

  public boolean hasExtTableConstraintsSql()
  {
    return informationSchemaQueries.containsKey(EXT_TABLE_CONSTRAINTS);
  }

  public boolean hasExtTablesSql()
  {
    return informationSchemaQueries.containsKey(EXT_TABLES);
  }

  public boolean hasOverrideTypeInfoSql()
  {
    return informationSchemaQueries.containsKey(OVERRIDE_TYPE_INFO);
  }

  public boolean hasRoutinesSql()
  {
    return informationSchemaQueries.containsKey(ROUTINES);
  }

  public boolean hasSchemataSql()
  {
    return informationSchemaQueries.containsKey(SCHEMATA);
  }

  public boolean hasSequencesSql()
  {
    return informationSchemaQueries.containsKey(SEQUENCES);
  }

  public boolean hasSynonymsSql()
  {
    return informationSchemaQueries.containsKey(EXT_SYNONYMS);
  }

  public boolean hasTableConstraintsColumnsSql()
  {
    return informationSchemaQueries.containsKey(CONSTRAINT_COLUMN_USAGE);
  }

  public boolean hasTableConstraintsSql()
  {
    return informationSchemaQueries.containsKey(TABLE_CONSTRAINTS);
  }

  public boolean hasTriggerSql()
  {
    return informationSchemaQueries.containsKey(TRIGGERS);
  }

  public boolean hasViewsSql()
  {
    return informationSchemaQueries.containsKey(VIEWS);
  }

  /**
   * Information schema views from a map.
   *
   * @param informationSchemaViewsSql
   *        Map of information schema view definitions.
   */
  public void loadResourceFolder(final String classpath)
  {
    for (final InformationSchemaKey key: InformationSchemaKey.values())
    {
      final String resource;
      if (classpath == null)
      {
        resource = key.getResource();
      }
      else
      {
        resource = String.format("%s/%s", classpath, key.getResource());
      }
      final String sql = readResourceFully(resource);
      if (!isBlank(sql))
      {
        informationSchemaQueries.put(key, sql);
      }
    }
  }

  /**
   * Sets the additional attributes SQL for columns.
   *
   * @param sql
   *        Additional attributes SQL for columns.
   */
  public void setAdditionalColumnAttributesSql(final String sql)
  {
    informationSchemaQueries.put(ADDITIONAL_COLUMN_ATTRIBUTES, sql);
  }

  /**
   * Sets the additional attributes SQL for tables.
   *
   * @param sql
   *        Additional attributes SQL for tables.
   */
  public void setAdditionalTableAttributesSql(final String sql)
  {
    informationSchemaQueries.put(ADDITIONAL_TABLE_ATTRIBUTES, sql);
  }

  /**
   * Sets the index definitions SQL.
   *
   * @param sql
   *        Index definitions SQL.
   */
  public void setExtIndexesSql(final String sql)
  {
    informationSchemaQueries.put(EXT_INDEXES, sql);
  }

  /**
   * Sets the table check constraints SQL.
   *
   * @param sql
   *        Table check constraints SQL.
   */
  public void setExtTableConstraintsSql(final String sql)
  {
    informationSchemaQueries.put(EXT_TABLE_CONSTRAINTS, sql);
  }

  /**
   * Sets the table definitions SQL.
   *
   * @param sql
   *        Table definitions SQL.
   */
  public void setExtTablesSql(final String sql)
  {
    informationSchemaQueries.put(EXT_TABLES, sql);
  }

  /**
   * Sets SQL that overrides DatabaseMetaData#getTypeInfo().
   * {@link DatabaseMetaData#getTypeInfo()}.
   *
   * @param sql
   *        SQL that overrides DatabaseMetaData#getTypeInfo().
   */
  public void setOverrideTypeInfoSql(final String sql)
  {
    informationSchemaQueries.put(OVERRIDE_TYPE_INFO, sql);
  }

  /**
   * Sets the procedure definitions SQL.
   *
   * @param sql
   *        Procedure definitions SQL.
   */
  public void setRoutinesSql(final String sql)
  {
    informationSchemaQueries.put(ROUTINES, sql);
  }

  /**
   * Sets the schemata SQL.
   *
   * @param sql
   *        Schemata SQL.
   */
  public void setSchemataSql(final String sql)
  {
    informationSchemaQueries.put(SCHEMATA, sql);
  }

  /**
   * Sets the sequences SQL.
   *
   * @param sql
   *        Sequences SQL.
   */
  public void setSequencesSql(final String sql)
  {
    informationSchemaQueries.put(SEQUENCES, sql);
  }

  /**
   * Sets the synonym SQL.
   *
   * @param sql
   *        Synonyms SQL.
   */
  public void setSynonymsSql(final String sql)
  {
    informationSchemaQueries.put(EXT_SYNONYMS, sql);
  }

  /**
   * Sets the table constraints columns SQL.
   *
   * @param sql
   *        Table constraints columns SQL.
   */
  public void setTableConstraintsColumnsSql(final String sql)
  {
    informationSchemaQueries.put(CONSTRAINT_COLUMN_USAGE, sql);
  }

  /**
   * Sets the table constraints SQL.
   *
   * @param sql
   *        Table constraints SQL.
   */
  public void setTableConstraintsSql(final String sql)
  {
    informationSchemaQueries.put(TABLE_CONSTRAINTS, sql);
  }

  /**
   * Sets the trigger definitions SQL.
   *
   * @param sql
   *        Trigger definitions SQL.
   */
  public void setTriggersSql(final String sql)
  {
    informationSchemaQueries.put(TRIGGERS, sql);
  }

  /**
   * Sets the view definitions SQL.
   *
   * @param sql
   *        View definitions SQL.
   */
  public void setViewsSql(final String sql)
  {
    informationSchemaQueries.put(VIEWS, sql);
  }

  public Config toConfig()
  {
    final Config config = new Config();
    for (final Map.Entry<InformationSchemaKey, String> sqlEntry: informationSchemaQueries
      .entrySet())
    {
      config.put(sqlEntry.getKey().getLookupKey(), sqlEntry.getValue());
    }

    return config;
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(informationSchemaQueries);
  }

}
