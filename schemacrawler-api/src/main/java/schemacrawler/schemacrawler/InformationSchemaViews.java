/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_COLUMNS;
import static schemacrawler.schemacrawler.InformationSchemaKey.TABLE_CONSTRAINTS;
import static schemacrawler.schemacrawler.InformationSchemaKey.TRIGGERS;
import static schemacrawler.schemacrawler.InformationSchemaKey.VIEWS;

import java.sql.DatabaseMetaData;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.utility.Query;
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
   * @param informationSchemaViewsQueries
   *        Map of information schema view definitions.
   */
  InformationSchemaViews(final Map<InformationSchemaKey, String> informationSchemaViewsQueries)
  {
    informationSchemaQueries = new HashMap<>();
    if (informationSchemaViewsQueries != null)
    {
      informationSchemaQueries.putAll(informationSchemaViewsQueries);
    }
  }

  /**
   * Gets the additional attributes SQL for columns, from the additional
   * configuration.
   *
   * @return Additional attributes SQL for columns.
   */
  public Query getAdditionalColumnAttributesSql()
  {
    return new Query(ADDITIONAL_COLUMN_ATTRIBUTES
      .name(), informationSchemaQueries.get(ADDITIONAL_COLUMN_ATTRIBUTES));
  }

  /**
   * Gets the additional attributes SQL for tables, from the additional
   * configuration.
   *
   * @return Additional attributes SQL for tables.
   */
  public Query getAdditionalTableAttributesSql()
  {
    return new Query(ADDITIONAL_TABLE_ATTRIBUTES.name(),
                     informationSchemaQueries.get(ADDITIONAL_TABLE_ATTRIBUTES));
  }

  /**
   * Gets the hidden table column definitions SQL from the additional
   * configuration.
   *
   * @return Hidden table column definitions SQL.
   */
  public Query getExtHiddenTableColumnsSql()
  {
    return new Query(EXT_HIDDEN_TABLE_COLUMNS.name(),
                     informationSchemaQueries.get(EXT_HIDDEN_TABLE_COLUMNS));
  }

  /**
   * Gets the index definitions SQL from the additional configuration.
   *
   * @return Index definitions SQL.
   */
  public Query getExtIndexesSql()
  {
    return new Query(EXT_INDEXES.name(),
                     informationSchemaQueries.get(EXT_INDEXES));
  }

  /**
   * Gets the table check constraints SQL from the additional
   * configuration.
   *
   * @return Table check constraints SQL.
   */
  public Query getExtTableConstraintsSql()
  {
    return new Query(EXT_TABLE_CONSTRAINTS.name(),
                     informationSchemaQueries.get(EXT_TABLE_CONSTRAINTS));
  }

  /**
   * Gets the table definitions SQL from the additional configuration.
   *
   * @return Table definitions SQL.
   */
  public Query getExtTablesSql()
  {
    return new Query(EXT_TABLES.name(),
                     informationSchemaQueries.get(EXT_TABLES));
  }

  /**
   * Gets the foreign keys SQL from the additional configuration.
   *
   * @return Foreign keys SQL.
   */
  public Query getForeignKeysSql()
  {
    return new Query(FOREIGN_KEYS.name(),
                     informationSchemaQueries.get(FOREIGN_KEYS));
  }

  /**
   * Gets the indexes SQL from the additional configuration.
   *
   * @return Indexes SQL.
   */
  public Query getIndexesSql()
  {
    return new Query(INDEXES.name(), informationSchemaQueries.get(INDEXES));
  }

  /**
   * SQL that overrides DatabaseMetaData#getTypeInfo().
   * {@link DatabaseMetaData#getTypeInfo()}
   *
   * @return SQL that overrides DatabaseMetaData#getTypeInfo().
   */
  public Query getOverrideTypeInfoSql()
  {
    return new Query(OVERRIDE_TYPE_INFO.name(),
                     informationSchemaQueries.get(OVERRIDE_TYPE_INFO));
  }

  /**
   * Gets the routine definitions SQL from the additional configuration.
   *
   * @return Routine definitions SQL.
   */
  public Query getRoutinesSql()
  {
    return new Query(ROUTINES.name(), informationSchemaQueries.get(ROUTINES));
  }

  /**
   * Gets the schemata SQL from the additional configuration.
   *
   * @return Schemata SQL.
   */
  public Query getSchemataSql()
  {
    return new Query(SCHEMATA.name(), informationSchemaQueries.get(SCHEMATA));
  }

  /**
   * Gets the sequences SQL from the additional configuration.
   *
   * @return Sequences SQL.
   */
  public Query getSequencesSql()
  {
    return new Query(SEQUENCES.name(), informationSchemaQueries.get(SEQUENCES));
  }

  /**
   * Gets the synonyms SQL from the additional configuration.
   *
   * @return Synonyms SQL.
   */
  public Query getSynonymsSql()
  {
    return new Query(EXT_SYNONYMS.name(),
                     informationSchemaQueries.get(EXT_SYNONYMS));
  }

  /**
   * Gets the table columns SQL from the additional configuration.
   *
   * @return Table columns SQL.
   */
  public Query getTableColumnsSql()
  {
    return new Query(TABLE_COLUMNS.name(),
                     informationSchemaQueries.get(TABLE_COLUMNS));
  }

  /**
   * Gets the table constraints columns SQL from the additional
   * configuration.
   *
   * @return Table constraints columns SQL.
   */
  public Query getTableConstraintsColumnsSql()
  {
    return new Query(CONSTRAINT_COLUMN_USAGE.name(),
                     informationSchemaQueries.get(CONSTRAINT_COLUMN_USAGE));
  }

  /**
   * Gets the table constraints SQL from the additional configuration.
   *
   * @return Table constraints SQL.
   */
  public Query getTableConstraintsSql()
  {
    return new Query(TABLE_CONSTRAINTS.name(),
                     informationSchemaQueries.get(TABLE_CONSTRAINTS));
  }

  /**
   * Gets the trigger definitions SQL from the additional configuration.
   *
   * @return Trigger definitions SQL.
   */
  public Query getTriggersSql()
  {
    return new Query(TRIGGERS.name(), informationSchemaQueries.get(TRIGGERS));
  }

  /**
   * Gets the view definitions SQL from the additional configuration.
   *
   * @return View definitions SQL.
   */
  public Query getViewsSql()
  {
    return new Query(VIEWS.name(), informationSchemaQueries.get(VIEWS));
  }

  public boolean hasAdditionalColumnAttributesSql()
  {
    return informationSchemaQueries.containsKey(ADDITIONAL_COLUMN_ATTRIBUTES);
  }

  public boolean hasAdditionalTableAttributesSql()
  {
    return informationSchemaQueries.containsKey(ADDITIONAL_TABLE_ATTRIBUTES);
  }

  public boolean hasExtHiddenTableColumnsSql()
  {
    return informationSchemaQueries.containsKey(EXT_HIDDEN_TABLE_COLUMNS);
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

  public boolean hasForeignKeysSql()
  {
    return informationSchemaQueries.containsKey(FOREIGN_KEYS);
  }

  public boolean hasIndexesSql()
  {
    return informationSchemaQueries.containsKey(INDEXES);
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

  public boolean hasTableColumnsSql()
  {
    return informationSchemaQueries.containsKey(TABLE_COLUMNS);
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

  public boolean isEmpty()
  {
    return informationSchemaQueries.isEmpty();
  }

  public int size()
  {
    return informationSchemaQueries.size();
  }

  @Override
  public String toString()
  {
    return ObjectToString.toString(informationSchemaQueries);
  }

}
