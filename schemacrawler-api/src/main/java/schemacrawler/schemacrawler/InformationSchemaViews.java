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
   * Gets the table check constraints SQL from the additional
   * configuration.
   * 
   * @return Table check constraints SQL.
   */
  public String getCheckConstraints()
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
  public String getRoutines()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_ROUTINES);
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   * 
   * @return Table constraints SQL.
   */
  public String getTableConstraints()
  {
    return informationSchemaQueries
      .get(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS);
  }

  /**
   * Gets the trigger definitions SQL from the additional configuration.
   * 
   * @return Trigger defnitions SQL.
   */
  public String getTriggers()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_TRIGGERS);
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   * 
   * @return View defnitions SQL.
   */
  public String getViews()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_VIEWS);
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
   * Sets the table check constraints SQL from the additional
   * configuration.
   * 
   * @param sql
   *        Table check constraints SQL.
   */
  public void setCheckConstraintsSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS, sql);
  }

  /**
   * Sets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @param sql
   *        Procedure defnitions SQL.
   */
  public void setRoutinesSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_ROUTINES, sql);
  }

  /**
   * Sets the table constraints SQL from the additional configuration.
   * 
   * @param sql
   *        Table constraints SQL.
   */
  public void setTableConstraintsSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS, sql);
  }

  /**
   * Sets the trigger definitions SQL from the additional configuration.
   * 
   * @param sql
   *        Trigger defnitions SQL.
   */
  public void setTriggersSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_TRIGGERS, sql);
  }

  /**
   * Sets the view definitions SQL from the additional configuration.
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
