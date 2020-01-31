/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Descriptor for level of schema detail.
 *
 * @author Sualeh Fatehi
 */
public final class SchemaInfoLevel
  implements Options
{

  private final boolean[] schemaInfoRetrievals;
  private final String tag;

  SchemaInfoLevel(final String tag,
                  final Map<SchemaInfoRetrieval, Boolean> schemaInfoRetrievalsMap)
  {
    requireNonNull(tag, "No tag provided");
    this.tag = tag;

    requireNonNull(schemaInfoRetrievalsMap,
                   "No schema info retrievals provided");
    final SchemaInfoRetrieval[] schemaInfoRetrievalsArray =
      SchemaInfoRetrieval.values();
    schemaInfoRetrievals = new boolean[schemaInfoRetrievalsArray.length];
    for (final SchemaInfoRetrieval schemaInfoRetrieval : schemaInfoRetrievalsArray)
    {
      final boolean schemaInfoRetrievalValue =
        schemaInfoRetrievalsMap.getOrDefault(schemaInfoRetrieval, false);
      schemaInfoRetrievals[schemaInfoRetrieval.ordinal()] =
        schemaInfoRetrievalValue;
    }
  }

  public String getTag()
  {
    return tag;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(schemaInfoRetrievals);
    result = prime * result + Objects.hash(tag);
    return result;
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof SchemaInfoLevel))
    {
      return false;
    }
    final SchemaInfoLevel other = (SchemaInfoLevel) obj;
    return Arrays.equals(schemaInfoRetrievals, other.schemaInfoRetrievals)
           && Objects.equals(tag, other.tag);
  }

  @Override
  public String toString()
  {
    final StringJoiner settings = new StringJoiner(System.lineSeparator());
    for (final SchemaInfoRetrieval schemaInfoRetrieval : SchemaInfoRetrieval.values())
    {
      settings.add(String.format("  %s=%b",
                                 schemaInfoRetrieval.name(),
                                 is(schemaInfoRetrieval)));
    }
    return String.format("SchemaInfoLevel <%s>%n{%n%s%n}%n", tag, settings);
  }

  public boolean is(final SchemaInfoRetrieval schemaInfoRetrieval)
  {
    if (schemaInfoRetrieval == null)
    {
      return false;
    }
    return schemaInfoRetrievals[schemaInfoRetrieval.ordinal()];
  }

  public boolean isRetrieveAdditionalColumnAttributes()
  {
    return is(SchemaInfoRetrieval.retrieveAdditionalColumnAttributes);
  }

  public boolean isRetrieveAdditionalColumnMetadata()
  {
    return is(SchemaInfoRetrieval.retrieveAdditionalColumnMetadata);
  }

  public boolean isRetrieveAdditionalDatabaseInfo()
  {
    return is(SchemaInfoRetrieval.retrieveAdditionalDatabaseInfo);
  }

  public boolean isRetrieveAdditionalJdbcDriverInfo()
  {
    return is(SchemaInfoRetrieval.retrieveAdditionalJdbcDriverInfo);
  }

  public boolean isRetrieveAdditionalTableAttributes()
  {
    return is(SchemaInfoRetrieval.retrieveAdditionalTableAttributes);
  }

  public boolean isRetrieveColumnDataTypes()
  {
    return is(SchemaInfoRetrieval.retrieveColumnDataTypes);
  }

  public boolean isRetrieveDatabaseInfo()
  {
    return is(SchemaInfoRetrieval.retrieveDatabaseInfo);
  }

  public boolean isRetrieveForeignKeyDefinitions()
  {
    return is(SchemaInfoRetrieval.retrieveForeignKeyDefinitions);
  }

  public boolean isRetrieveForeignKeys()
  {
    return is(SchemaInfoRetrieval.retrieveForeignKeys);
  }

  public boolean isRetrieveIndexColumnInformation()
  {
    return is(SchemaInfoRetrieval.retrieveIndexColumnInformation);
  }

  public boolean isRetrieveIndexes()
  {
    return is(SchemaInfoRetrieval.retrieveIndexes);
  }

  public boolean isRetrieveIndexInformation()
  {
    return is(SchemaInfoRetrieval.retrieveIndexInformation);
  }

  public boolean isRetrievePrimaryKeyDefinitions()
  {
    return is(SchemaInfoRetrieval.retrievePrimaryKeyDefinitions);
  }

  public boolean isRetrieveRoutineParameters()
  {
    return is(SchemaInfoRetrieval.retrieveRoutineParameters);
  }

  public boolean isRetrieveRoutineInformation()
  {
    return is(SchemaInfoRetrieval.retrieveRoutineInformation);
  }

  public boolean isRetrieveRoutines()
  {
    return is(SchemaInfoRetrieval.retrieveRoutines);
  }

  public boolean isRetrieveSequenceInformation()
  {
    return is(SchemaInfoRetrieval.retrieveSequenceInformation);
  }

  public boolean isRetrieveServerInfo()
  {
    return is(SchemaInfoRetrieval.retrieveServerInfo);
  }

  public boolean isRetrieveSynonymInformation()
  {
    return is(SchemaInfoRetrieval.retrieveSynonymInformation);
  }

  public boolean isRetrieveTableColumnPrivileges()
  {
    return is(SchemaInfoRetrieval.retrieveTableColumnPrivileges);
  }

  public boolean isRetrieveTableColumns()
  {
    return is(SchemaInfoRetrieval.retrieveTableColumns);
  }

  public boolean isRetrieveTableConstraintDefinitions()
  {
    return is(SchemaInfoRetrieval.retrieveTableConstraintDefinitions);
  }

  public boolean isRetrieveTableConstraintInformation()
  {
    return is(SchemaInfoRetrieval.retrieveTableConstraintInformation);
  }

  public boolean isRetrieveTableDefinitionsInformation()
  {
    return is(SchemaInfoRetrieval.retrieveTableDefinitionsInformation);
  }

  public boolean isRetrieveTablePrivileges()
  {
    return is(SchemaInfoRetrieval.retrieveTablePrivileges);
  }

  public boolean isRetrieveTables()
  {
    return is(SchemaInfoRetrieval.retrieveTables);
  }

  public boolean isRetrieveTriggerInformation()
  {
    return is(SchemaInfoRetrieval.retrieveTriggerInformation);
  }

  public boolean isRetrieveUserDefinedColumnDataTypes()
  {
    return is(SchemaInfoRetrieval.retrieveUserDefinedColumnDataTypes);
  }

  public boolean isRetrieveViewInformation()
  {
    return is(SchemaInfoRetrieval.retrieveViewInformation);
  }

}
