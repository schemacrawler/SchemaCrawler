/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import java.util.HashMap;
import java.util.Map;

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
  private static final String KEY_GET_INDEX_INFO = "getIndexInfo";
  private static final String KEY_INFORMATION_SCHEMA_ROUTINES = "select.INFORMATION_SCHEMA.ROUTINES";
  private static final String KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS = "select.INFORMATION_SCHEMA.CHECK_CONSTRAINTS";

  private final Map<String, Query> informationSchemaQueries;

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
  public InformationSchemaViews(final Map<String, String> informationSchemaViewsSql)
  {
    this.informationSchemaQueries = new HashMap<String, Query>();
    if (informationSchemaViewsSql != null)
    {
      final String[] keys = new String[] {
          KEY_INFORMATION_SCHEMA_VIEWS,
          KEY_INFORMATION_SCHEMA_TRIGGERS,
          KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS,
          KEY_GET_INDEX_INFO,
          KEY_INFORMATION_SCHEMA_ROUTINES,
          KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS,
      };
      for (final String key: keys)
      {
        if (informationSchemaViewsSql.containsKey(key))
        {
          try
          {
            final Query query = new Query(key, informationSchemaViewsSql
              .get(key));
            this.informationSchemaQueries.put(key, query);
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
  public Query getCheckConstraints()
  {
    return informationSchemaQueries
      .get(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS);
  }

  /**
   * Gets the index info SQL from the additional configuration.
   * 
   * @return Index info constraints SQL.
   */
  public Query getIndexInfo()
  {
    return informationSchemaQueries.get(KEY_GET_INDEX_INFO);
  }

  /**
   * Gets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @return Procedure defnitions SQL.
   */
  public Query getRoutines()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_ROUTINES);
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   * 
   * @return Table constraints SQL.
   */
  public Query getTableConstraints()
  {
    return informationSchemaQueries
      .get(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS);
  }

  /**
   * Gets the trigger definitions SQL from the additional configuration.
   * 
   * @return Trigger defnitions SQL.
   */
  public Query getTriggers()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_TRIGGERS);
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   * 
   * @return View defnitions SQL.
   */
  public Query getViews()
  {
    return informationSchemaQueries.get(KEY_INFORMATION_SCHEMA_VIEWS);
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
    informationSchemaQueries
      .put(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS,
           new Query(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS, sql));
  }

  /**
   * Sets the index info SQL from the additional configuration.
   * 
   * @param sql
   *        Index info constraints SQL.
   */
  public void setIndexInfoSql(final String sql)
  {
    informationSchemaQueries.put(KEY_GET_INDEX_INFO,
                                 new Query(KEY_GET_INDEX_INFO, sql));
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
    informationSchemaQueries
      .put(KEY_INFORMATION_SCHEMA_ROUTINES,
           new Query(KEY_INFORMATION_SCHEMA_ROUTINES, sql));
  }

  /**
   * Sets the table constraints SQL from the additional configuration.
   * 
   * @param sql
   *        Table constraints SQL.
   */
  public void setTableConstraintsSql(final String sql)
  {
    informationSchemaQueries
      .put(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS,
           new Query(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS, sql));
  }

  /**
   * Sets the trigger definitions SQL from the additional configuration.
   * 
   * @param sql
   *        Trigger defnitions SQL.
   */
  public void setTriggersSql(final String sql)
  {
    informationSchemaQueries
      .put(KEY_INFORMATION_SCHEMA_TRIGGERS,
           new Query(KEY_INFORMATION_SCHEMA_TRIGGERS, sql));
  }

  /**
   * Sets the view definitions SQL from the additional configuration.
   * 
   * @param sql
   *        View defnitions SQL.
   */
  public void setViewsSql(final String sql)
  {
    informationSchemaQueries.put(KEY_INFORMATION_SCHEMA_VIEWS,
                                 new Query(KEY_INFORMATION_SCHEMA_VIEWS, sql));
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return informationSchemaQueries.toString();
  }

  boolean hasCheckConstraintsSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_CHECK_CONSTRAINTS);
  }

  boolean hasIndexInfoSql()
  {
    return informationSchemaQueries.containsKey(KEY_GET_INDEX_INFO);
  }

  boolean hasRoutinesSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_ROUTINES);
  }

  boolean hasTableConstraintsSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_TABLE_CONSTRAINTS);
  }

  boolean hasTriggerSql()
  {
    return informationSchemaQueries
      .containsKey(KEY_INFORMATION_SCHEMA_TRIGGERS);
  }

  boolean hasViewsSql()
  {
    return informationSchemaQueries.containsKey(KEY_INFORMATION_SCHEMA_VIEWS);
  }

}
