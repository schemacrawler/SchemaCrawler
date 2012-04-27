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

  private static final long serialVersionUID = 3587581365346059044L;

  private static final String KEY_INFORMATION_SCHEMA_VIEWS = "select.INFORMATION_SCHEMA.VIEWS";
  private static final String KEY_INFORMATION_SCHEMA_TRIGGERS = "select.INFORMATION_SCHEMA.TRIGGERS";
  private static final String KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS = "select.INFORMATION_SCHEMA.TABLE_CONSTRAINTS";
  private static final String KEY_INFORMATION_SCHEMA_ROUTINES = "select.INFORMATION_SCHEMA.ROUTINES";
  private static final String KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS = "select.INFORMATION_SCHEMA.CHECK_CONSTRAINTS";
  private static final String KEY_INFORMATION_SCHEMA_SCHEMATA = "select.INFORMATION_SCHEMA.SCHEMATA";
  private static final String KEY_ADDITIONAL_TABLE_ATTRIBUTES = "select.ADDITIONAL_TABLE_ATTRIBUTES";
  private static final String KEY_ADDITIONAL_COLUMN_ATTRIBUTES = "select.ADDITIONAL_COLUMN_ATTRIBUTES";

  private final Map<String, String> informationSchemaQueries;

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
    informationSchemaQueries = new HashMap<String, String>();
    if (informationSchemaViewsSql != null)
    {
      final String[] keys = new String[] {
          KEY_INFORMATION_SCHEMA_VIEWS,
          KEY_INFORMATION_SCHEMA_TRIGGERS,
          KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS,
          KEY_INFORMATION_SCHEMA_ROUTINES,
          KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS,
          KEY_INFORMATION_SCHEMA_SCHEMATA,
          KEY_ADDITIONAL_TABLE_ATTRIBUTES,
          KEY_ADDITIONAL_COLUMN_ATTRIBUTES
      };
      for (final String key: keys)
      {
        if (informationSchemaViewsSql.containsKey(key))
        {
          try
          {
            informationSchemaQueries.put(key,
                                         informationSchemaViewsSql.get(key));
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
    return informationSchemaQueries.get(KEY_ADDITIONAL_COLUMN_ATTRIBUTES);
  }

  /**
   * Gets the additional attributes SQL for tables, from the additional
   * configuration.
   * 
   * @return Additional attributes SQL for tables.
   */
  public String getAdditionalTableAttributesSql()
  {
    return informationSchemaQueries.get(KEY_ADDITIONAL_TABLE_ATTRIBUTES);
  }

  /**
   * Gets the table check constraints SQL from the additional
   * configuration.
   * 
   * @return Table check constraints SQL.
   */
  public String getCheckConstraintsSql()
  {
    return informationSchemaQueries
      .get(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS);
  }

  /**
   * Gets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @return Procedure defnitions SQL.
   */
  public String getRoutinesSql()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_ROUTINES);
  }

  /**
   * Gets the schemata SQL from the additional configuration.
   * 
   * @return Schemata SQL.
   */
  public String getSchemataSql()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_SCHEMATA);
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   * 
   * @return Table constraints SQL.
   */
  public String getTableConstraintsSql()
  {
    return informationSchemaQueries
      .get(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS);
  }

  /**
   * Gets the trigger definitions SQL from the additional configuration.
   * 
   * @return Trigger defnitions SQL.
   */
  public String getTriggersSql()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_TRIGGERS);
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   * 
   * @return View defnitions SQL.
   */
  public String getViewsSql()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_VIEWS);
  }

  public boolean hasAdditionalColumnAttributesSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_ADDITIONAL_COLUMN_ATTRIBUTES);
  }

  public boolean hasAdditionalTableAttributesSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_ADDITIONAL_TABLE_ATTRIBUTES);
  }

  public boolean hasCheckConstraintsSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS);
  }

  public boolean hasRoutinesSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_ROUTINES);
  }

  public boolean hasSchemataSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_SCHEMATA);
  }

  public boolean hasTableConstraintsSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS);
  }

  public boolean hasTriggerSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_TRIGGERS);
  }

  public boolean hasViewsSql()
  {
    return informationSchemaQueries.containsKey(KEY_INFORMATION_SCHEMA_VIEWS);
  }

  /**
   * Sets the additional attributes SQL for columns.
   * 
   * @param sql
   *        Additional attributes SQL for columns.
   */
  public void setAdditionalColumnAttributesSql(final String sql)
  {
    informationSchemaQueries.put(KEY_ADDITIONAL_COLUMN_ATTRIBUTES, sql);
  }

  /**
   * Sets the additional attributes SQL for tables.
   * 
   * @param sql
   *        Additional attributes SQL for tables.
   */
  public void setAdditionalTableAttributesSql(final String sql)
  {
    informationSchemaQueries.put(KEY_ADDITIONAL_TABLE_ATTRIBUTES, sql);
  }

  /**
   * Sets the table check constraints SQL.
   * 
   * @param sql
   *        Table check constraints SQL.
   */
  public void setCheckConstraintsSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS, sql);
  }

  /**
   * Sets the procedure definitions SQL.
   * 
   * @param sql
   *        Procedure definitions SQL.
   */
  public void setRoutinesSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_ROUTINES, sql);
  }

  /**
   * Sets the schemata SQL.
   * 
   * @param sql
   *        Schemata SQL.
   */
  public void setSchemataSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_SCHEMATA, sql);
  }

  /**
   * Sets the table constraints SQL.
   * 
   * @param sql
   *        Table constraints SQL.
   */
  public void setTableConstraintsSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS, sql);
  }

  /**
   * Sets the trigger definitions SQL.
   * 
   * @param sql
   *        Trigger definitions SQL.
   */
  public void setTriggersSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_TRIGGERS, sql);
  }

  /**
   * Sets the view definitions SQL.
   * 
   * @param sql
   *        View defnitions SQL.
   */
  public void setViewsSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_VIEWS, sql);
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(informationSchemaQueries);
  }

}
