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


import static sf.util.Utility.toSnakeCase;

public enum SchemaInfoRetrieval
{
 retrieveAdditionalColumnAttributes,
 retrieveAdditionalDatabaseInfo,
 retrieveAdditionalJdbcDriverInfo,
 retrieveAdditionalTableAttributes,
 retrieveColumnDataTypes,
 retrieveDatabaseInfo,
 retrieveForeignKeyDefinitions,
 retrieveForeignKeys,
 retrieveIndexColumnInformation,
 retrieveIndexes,
 retrieveIndexInformation,
 retrievePrimaryKeyDefinitions,
 retrieveRoutineColumns,
 retrieveRoutineInformation,
 retrieveRoutines,
 retrieveSequenceInformation,
 retrieveServerInfo,
 retrieveSynonymInformation,
 retrieveTableColumnPrivileges,
 retrieveTableColumns,
 retrieveTableConstraintDefinitions,
 retrieveTableConstraintInformation,
 retrieveTableDefinitionsInformation,
 retrieveTablePrivileges,
 retrieveTables,
 retrieveTriggerInformation,
 retrieveUserDefinedColumnDataTypes,
 retrieveViewInformation;

  private static final String SC_SCHEMA_INFO_LEVEL = "schemacrawler.schema_info_level.";

  public String getKey()
  {
    return SC_SCHEMA_INFO_LEVEL + toSnakeCase(name());
  }

}
