/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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

package schemacrawler.schemacrawler;


import static schemacrawler.schemacrawler.InformationSchemaKey.ADDITIONAL_COLUMN_ATTRIBUTES;
import static schemacrawler.schemacrawler.InformationSchemaKey.ADDITIONAL_TABLE_ATTRIBUTES;
import static schemacrawler.schemacrawler.InformationSchemaKey.CONSTRAINT_COLUMN_USAGE;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_HIDDEN_TABLE_COLUMNS;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_INDEXES;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_SYNONYMS;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_TABLES;
import static schemacrawler.schemacrawler.InformationSchemaKey.EXT_TABLE_CONSTRAINTS;
import static schemacrawler.schemacrawler.InformationSchemaKey.FOREIGN_KEYS;
import static schemacrawler.schemacrawler.InformationSchemaKey.INDEXES;
import static schemacrawler.schemacrawler.InformationSchemaKey.OVERRIDE_TYPE_INFO;
import static schemacrawler.schemacrawler.InformationSchemaKey.ROUTINES;
import static schemacrawler.schemacrawler.InformationSchemaKey.SCHEMATA;
import static schemacrawler.schemacrawler.InformationSchemaKey.SEQUENCES;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_CONSTRAINTS;
import static schemacrawler.schemacrawler.InformationSchemaKey.TRIGGERS;
import static schemacrawler.schemacrawler.InformationSchemaKey.VIEWS;
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
public final class InformationSchemaViewsBuilder
  implements OptionsBuilder<InformationSchemaViews>
{

  private final Map<InformationSchemaKey, String> informationSchemaQueries;

  public InformationSchemaViewsBuilder()
  {
    informationSchemaQueries = new HashMap<>();
  }

  /**
   * Information schema views from a map.
   *
   * @param informationSchemaViewsSql
   *        Map of information schema view definitions.
   */
  @Override
  public InformationSchemaViewsBuilder fromConfig(final Map<String, String> informationSchemaViewsSql)
  {
    if (informationSchemaViewsSql == null)
    {
      return this;
    }

    for (final InformationSchemaKey key: InformationSchemaKey.values())
    {
      if (informationSchemaViewsSql.containsKey(key.getLookupKey()))
      {
        try
        {
          informationSchemaQueries
            .put(key, informationSchemaViewsSql.get(key.getLookupKey()));
        }
        catch (final IllegalArgumentException e)
        {
          // Ignore
        }
      }
    }

    return this;
  }

  /**
   * Information schema views from a map.
   *
   * @param informationSchemaViewsSql
   *        Map of information schema view definitions.
   */
  public InformationSchemaViewsBuilder fromResourceFolder(final String classpath)
  {
    informationSchemaQueries.clear();

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

    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InformationSchemaViews toOptions()
  {
    return new InformationSchemaViews(informationSchemaQueries);
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(informationSchemaQueries);
  }

  /**
   * Sets the additional attributes SQL for columns.
   *
   * @param sql
   *        Additional attributes SQL for columns.
   */
  public InformationSchemaViewsBuilder withAdditionalColumnAttributesSql(final String sql)
  {
    informationSchemaQueries.put(ADDITIONAL_COLUMN_ATTRIBUTES, sql);
    return this;
  }

  /**
   * Sets the additional attributes SQL for tables.
   *
   * @param sql
   *        Additional attributes SQL for tables.
   */
  public InformationSchemaViewsBuilder withAdditionalTableAttributesSql(final String sql)
  {
    informationSchemaQueries.put(ADDITIONAL_TABLE_ATTRIBUTES, sql);
    return this;
  }

  /**
   * Sets the hidden table column definitions SQL.
   *
   * @param sql
   *        Hidden table column definitions SQL.
   */
  public InformationSchemaViewsBuilder withExtHiddenTableColumnsSql(final String sql)
  {
    informationSchemaQueries.put(EXT_HIDDEN_TABLE_COLUMNS, sql);
    return this;
  }

  /**
   * Sets the index definitions SQL.
   *
   * @param sql
   *        Index definitions SQL.
   */
  public InformationSchemaViewsBuilder withExtIndexesSql(final String sql)
  {
    informationSchemaQueries.put(EXT_INDEXES, sql);
    return this;
  }

  /**
   * Sets the table check constraints SQL.
   *
   * @param sql
   *        Table check constraints SQL.
   */
  public InformationSchemaViewsBuilder withExtTableConstraintsSql(final String sql)
  {
    informationSchemaQueries.put(EXT_TABLE_CONSTRAINTS, sql);
    return this;
  }

  /**
   * Sets the table definitions SQL.
   *
   * @param sql
   *        Table definitions SQL.
   */
  public InformationSchemaViewsBuilder withExtTablesSql(final String sql)
  {
    informationSchemaQueries.put(EXT_TABLES, sql);
    return this;
  }

  /**
   * Sets the foreign key SQL.
   *
   * @param sql
   *        Foreign key SQL.
   */
  public InformationSchemaViewsBuilder withForeignKeysSql(final String sql)
  {
    informationSchemaQueries.put(FOREIGN_KEYS, sql);
    return this;
  }

  /**
   * Sets the indexes SQL.
   *
   * @param sql
   *        Indexes SQL.
   */
  public InformationSchemaViewsBuilder withIndexesSql(final String sql)
  {
    informationSchemaQueries.put(INDEXES, sql);
    return this;
  }

  /**
   * Sets SQL that overrides DatabaseMetaData#getTypeInfo().
   * {@link DatabaseMetaData#getTypeInfo()}.
   *
   * @param sql
   *        SQL that overrides DatabaseMetaData#getTypeInfo().
   */
  public InformationSchemaViewsBuilder withOverrideTypeInfoSql(final String sql)
  {
    informationSchemaQueries.put(OVERRIDE_TYPE_INFO, sql);
    return this;
  }

  /**
   * Sets the procedure definitions SQL.
   *
   * @param sql
   *        Procedure definitions SQL.
   */
  public InformationSchemaViewsBuilder withRoutinesSql(final String sql)
  {
    informationSchemaQueries.put(ROUTINES, sql);
    return this;
  }

  /**
   * Sets the schemata SQL.
   *
   * @param sql
   *        Schemata SQL.
   */
  public InformationSchemaViewsBuilder withSchemataSql(final String sql)
  {
    informationSchemaQueries.put(SCHEMATA, sql);
    return this;
  }

  /**
   * Sets the sequences SQL.
   *
   * @param sql
   *        Sequences SQL.
   */
  public InformationSchemaViewsBuilder withSequencesSql(final String sql)
  {
    informationSchemaQueries.put(SEQUENCES, sql);
    return this;
  }

  /**
   * Sets the synonym SQL.
   *
   * @param sql
   *        Synonyms SQL.
   */
  public InformationSchemaViewsBuilder withSynonymsSql(final String sql)
  {
    informationSchemaQueries.put(EXT_SYNONYMS, sql);
    return this;
  }

  /**
   * Sets the table constraints columns SQL.
   *
   * @param sql
   *        Table constraints columns SQL.
   */
  public InformationSchemaViewsBuilder withTableConstraintsColumnsSql(final String sql)
  {
    informationSchemaQueries.put(CONSTRAINT_COLUMN_USAGE, sql);
    return this;
  }

  /**
   * Sets the table constraints SQL.
   *
   * @param sql
   *        Table constraints SQL.
   */
  public InformationSchemaViewsBuilder withTableConstraintsSql(final String sql)
  {
    informationSchemaQueries.put(TABLE_CONSTRAINTS, sql);
    return this;
  }

  /**
   * Sets the trigger definitions SQL.
   *
   * @param sql
   *        Trigger definitions SQL.
   */
  public InformationSchemaViewsBuilder withTriggersSql(final String sql)
  {
    informationSchemaQueries.put(TRIGGERS, sql);
    return this;
  }

  /**
   * Sets the view definitions SQL.
   *
   * @param sql
   *        View definitions SQL.
   */
  public InformationSchemaViewsBuilder withViewsSql(final String sql)
  {
    informationSchemaQueries.put(VIEWS, sql);
    return this;
  }

}
