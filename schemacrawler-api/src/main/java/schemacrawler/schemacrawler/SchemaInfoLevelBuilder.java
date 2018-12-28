/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import sf.util.SchemaCrawlerLogger;

public final class SchemaInfoLevelBuilder
  implements OptionsBuilder<SchemaInfoLevelBuilder, SchemaInfoLevel>
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SchemaInfoLevelBuilder.class.getName());

  public static SchemaInfoLevelBuilder builder()
  {
    return new SchemaInfoLevelBuilder();
  }

  /**
   * Creates a new SchemaInfoLevel with settings for detailed schema
   * information.
   *
   * @return SchemaInfoLevel detailed
   */
  public static SchemaInfoLevel detailed()
  {
    return builder().withDetailed().toOptions();
  }

  /**
   * Creates a new SchemaInfoLevel with settings for maximum schema
   * information.
   *
   * @return SchemaInfoLevel maximum
   */
  public static SchemaInfoLevel maximum()
  {
    return builder().withMaximum().toOptions();
  }

  /**
   * Creates a new SchemaInfoLevel with settings for minimum schema
   * information.
   *
   * @return SchemaInfoLevel minimum
   */
  public static SchemaInfoLevel minimum()
  {
    return builder().withMinimum().toOptions();
  }

  /**
   * Retrieves schema based on standard options.
   *
   * @return Standard schema info level.
   */
  public static SchemaInfoLevel newSchemaInfoLevel()
  {
    return builder().withStandard().toOptions();
  }

  /**
   * Creates a new SchemaInfoLevel with settings for standard schema
   * information.
   *
   * @return SchemaInfoLevel standard
   */
  public static SchemaInfoLevel standard()
  {
    return builder().withStandard().toOptions();
  }

  private String tag;

  private final Map<SchemaInfoRetrieval, Boolean> schemaInfoRetrievals;

  private SchemaInfoLevelBuilder()
  {
    // Retrieve nothing
    schemaInfoRetrievals = new HashMap<>();
  }

  @Override
  public SchemaInfoLevelBuilder fromConfig(final Config config)
  {
    if (config == null)
    {
      return this;
    }

    for (final SchemaInfoRetrieval schemaInfoRetrieval: SchemaInfoRetrieval
      .values())
    {
      if (config.containsKey(schemaInfoRetrieval.getKey()))
      {
        final boolean booleanValue = config
          .getBooleanValue(schemaInfoRetrieval.getKey());
        schemaInfoRetrievals.put(schemaInfoRetrieval, booleanValue);
      }
    }

    return this;
  }

  @Override
  public SchemaInfoLevelBuilder fromOptions(final SchemaInfoLevel schemaInfoLevel)
  {
    if (schemaInfoLevel == null)
    {
      return this;
    }

    tag = schemaInfoLevel.getTag();

    try
    {
      final Method[] methods = SchemaInfoLevel.class.getDeclaredMethods();
      for (final Method method: methods)
      {
        if (method.getReturnType().isAssignableFrom(boolean.class)
            && method.getName().startsWith("isRetrieve"))
        {
          final String schemaInfoRetrievalName = "retrieve" + method.getName()
            .substring(10);
          final SchemaInfoRetrieval schemaInfoRetrieval = Enum
            .valueOf(SchemaInfoRetrieval.class, schemaInfoRetrievalName);
          final boolean booleanValue = (boolean) method.invoke(schemaInfoLevel);
          schemaInfoRetrievals.put(schemaInfoRetrieval, booleanValue);
        }
      }
    }
    catch (final Exception e)
    {
      LOGGER
        .log(Level.WARNING, "Could not obtain schema info level settings", e);
    }

    return this;
  }

  public String getTag()
  {
    return tag;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalColumnAttributes(final boolean retrieveAdditionalColumnAttributes)
  {
    schemaInfoRetrievals
      .put(SchemaInfoRetrieval.retrieveAdditionalColumnAttributes,
           retrieveAdditionalColumnAttributes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalDatabaseInfo(final boolean retrieveAdditionalDatabaseInfo)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveAdditionalDatabaseInfo,
                             retrieveAdditionalDatabaseInfo);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalJdbcDriverInfo(final boolean retrieveAdditionalJdbcDriverInfo)
  {
    schemaInfoRetrievals
      .put(SchemaInfoRetrieval.retrieveAdditionalJdbcDriverInfo,
           retrieveAdditionalJdbcDriverInfo);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalTableAttributes(final boolean retrieveAdditionalTableAttributes)
  {
    schemaInfoRetrievals
      .put(SchemaInfoRetrieval.retrieveAdditionalTableAttributes,
           retrieveAdditionalTableAttributes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveColumnDataTypes(final boolean retrieveColumnDataTypes)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveColumnDataTypes,
                             retrieveColumnDataTypes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveDatabaseInfo(final boolean retrieveDatabaseInfo)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveDatabaseInfo,
                             retrieveDatabaseInfo);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveForeignKeyDefinitions(final boolean retrieveForeignKeyDefinitions)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveForeignKeyDefinitions,
                             retrieveForeignKeyDefinitions);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveForeignKeys(final boolean retrieveForeignKeys)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveForeignKeys,
                             retrieveForeignKeys);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveIndexColumnInformation(final boolean retrieveIndexColumnInformation)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveIndexColumnInformation,
                             retrieveIndexColumnInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveIndexes(final boolean retrieveIndexes)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveIndexes,
                             retrieveIndexes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveIndexInformation(final boolean retrieveIndexInformation)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveIndexInformation,
                             retrieveIndexInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrievePrimaryKeyDefinitions(final boolean retrievePrimaryKeyDefinitions)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrievePrimaryKeyDefinitions,
                             retrievePrimaryKeyDefinitions);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutineColumns(final boolean retrieveRoutineColumns)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveRoutineColumns,
                             retrieveRoutineColumns);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutineInformation(final boolean retrieveRoutineInformation)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveRoutineInformation,
                             retrieveRoutineInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutines(final boolean retrieveRoutines)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveRoutines,
                             retrieveRoutines);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveSequenceInformation(final boolean retrieveSequenceInformation)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveSequenceInformation,
                             retrieveSequenceInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveServerInfo(final boolean retrieveServerInfo)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveServerInfo,
                             retrieveServerInfo);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveSynonymInformation(final boolean retrieveSynonymInformation)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveSynonymInformation,
                             retrieveSynonymInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableColumnPrivileges(final boolean retrieveTableColumnPrivileges)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveTableColumnPrivileges,
                             retrieveTableColumnPrivileges);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableColumns(final boolean retrieveTableColumns)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveTableColumns,
                             retrieveTableColumns);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableConstraintDefinitions(final boolean retrieveTableConstraintDefinitions)
  {
    schemaInfoRetrievals
      .put(SchemaInfoRetrieval.retrieveTableConstraintDefinitions,
           retrieveTableConstraintDefinitions);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableConstraintInformation(final boolean retrieveTableConstraintInformation)
  {
    schemaInfoRetrievals
      .put(SchemaInfoRetrieval.retrieveTableConstraintInformation,
           retrieveTableConstraintInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableDefinitionsInformation(final boolean retrieveTableDefinitionsInformation)
  {
    schemaInfoRetrievals
      .put(SchemaInfoRetrieval.retrieveTableDefinitionsInformation,
           retrieveTableDefinitionsInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTablePrivileges(final boolean retrieveTablePrivileges)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveTablePrivileges,
                             retrieveTablePrivileges);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTables(final boolean retrieveTables)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveTables,
                             retrieveTables);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTriggerInformation(final boolean retrieveTriggerInformation)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveTriggerInformation,
                             retrieveTriggerInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveUserDefinedColumnDataTypes(final boolean retrieveUserDefinedColumnDataTypes)
  {
    schemaInfoRetrievals
      .put(SchemaInfoRetrieval.retrieveUserDefinedColumnDataTypes,
           retrieveUserDefinedColumnDataTypes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveViewInformation(final boolean retrieveViewInformation)
  {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveViewInformation,
                             retrieveViewInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setTag(final String tag)
  {
    this.tag = tag;
    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SchemaInfoLevel toOptions()
  {
    return new SchemaInfoLevel(tag,
                               get(SchemaInfoRetrieval.retrieveTables),
                               get(SchemaInfoRetrieval.retrieveRoutines),
                               get(SchemaInfoRetrieval.retrieveColumnDataTypes),
                               get(SchemaInfoRetrieval.retrieveDatabaseInfo),
                               get(SchemaInfoRetrieval.retrieveAdditionalDatabaseInfo),
                               get(SchemaInfoRetrieval.retrieveServerInfo),
                               get(SchemaInfoRetrieval.retrieveAdditionalJdbcDriverInfo),
                               get(SchemaInfoRetrieval.retrieveUserDefinedColumnDataTypes),
                               get(SchemaInfoRetrieval.retrieveRoutineColumns),
                               get(SchemaInfoRetrieval.retrieveRoutineInformation),
                               get(SchemaInfoRetrieval.retrieveTableConstraintInformation),
                               get(SchemaInfoRetrieval.retrieveTableConstraintDefinitions),
                               get(SchemaInfoRetrieval.retrieveViewInformation),
                               get(SchemaInfoRetrieval.retrieveIndexInformation),
                               get(SchemaInfoRetrieval.retrieveIndexColumnInformation),
                               get(SchemaInfoRetrieval.retrievePrimaryKeyDefinitions),
                               get(SchemaInfoRetrieval.retrieveForeignKeys),
                               get(SchemaInfoRetrieval.retrieveForeignKeyDefinitions),
                               get(SchemaInfoRetrieval.retrieveIndexes),
                               get(SchemaInfoRetrieval.retrieveTablePrivileges),
                               get(SchemaInfoRetrieval.retrieveTableColumnPrivileges),
                               get(SchemaInfoRetrieval.retrieveTriggerInformation),
                               get(SchemaInfoRetrieval.retrieveSynonymInformation),
                               get(SchemaInfoRetrieval.retrieveSequenceInformation),
                               get(SchemaInfoRetrieval.retrieveTableColumns),
                               get(SchemaInfoRetrieval.retrieveAdditionalTableAttributes),
                               get(SchemaInfoRetrieval.retrieveAdditionalColumnAttributes),
                               get(SchemaInfoRetrieval.retrieveTableDefinitionsInformation));
  }

  @Override
  public String toString()
  {
    return tag == null? "": tag;
  }

  public SchemaInfoLevelBuilder withDetailed()
  {
    withStandard();
    setRetrieveUserDefinedColumnDataTypes(true);
    setRetrieveTriggerInformation(true);
    setRetrieveTableConstraintInformation(true);
    setRetrieveTableConstraintDefinitions(true);
    setRetrieveViewInformation(true);
    setRetrieveRoutineInformation(true);
    setTag("detailed");
    return this;
  }

  /**
   * Creates a new SchemaInfoLevel builder with settings for maximum
   * schema information.
   *
   * @return SchemaInfoLevel builder
   */
  public SchemaInfoLevelBuilder withMaximum()
  {
    withDetailed();
    setRetrieveAdditionalDatabaseInfo(true);
    setRetrieveServerInfo(true);
    setRetrieveAdditionalJdbcDriverInfo(true);
    setRetrieveTablePrivileges(true);
    setRetrieveTableColumnPrivileges(true);
    setRetrieveTableDefinitionsInformation(true);
    setRetrieveForeignKeyDefinitions(true);
    setRetrievePrimaryKeyDefinitions(true);
    setRetrieveAdditionalTableAttributes(true);
    setRetrieveAdditionalColumnAttributes(true);
    setRetrieveIndexInformation(true);
    setRetrieveIndexColumnInformation(true);
    setRetrieveSequenceInformation(true);
    setRetrieveSynonymInformation(true);
    setTag("maximum");
    return this;
  }

  public SchemaInfoLevelBuilder withMinimum()
  {
    setRetrieveDatabaseInfo(true);
    setRetrieveTables(true);
    setRetrieveRoutines(true);
    setTag("minimum");
    return this;
  }

  public SchemaInfoLevelBuilder withStandard()
  {
    withMinimum();
    setRetrieveColumnDataTypes(true);
    setRetrieveTableColumns(true);
    setRetrieveForeignKeys(true);
    setRetrieveIndexes(true);
    setRetrieveRoutineColumns(true);
    setTag("standard");
    return this;
  }

  private Boolean get(final SchemaInfoRetrieval schemaInfoRetrieval)
  {
    if (schemaInfoRetrieval == null)
    {
      return false;
    }
    return schemaInfoRetrievals.getOrDefault(schemaInfoRetrieval, false);
  }

}
