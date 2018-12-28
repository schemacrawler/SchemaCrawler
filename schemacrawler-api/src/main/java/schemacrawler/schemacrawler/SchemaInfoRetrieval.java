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
 retrieveAdditionalColumnAttributes(InfoLevel.maximum),
 retrieveAdditionalDatabaseInfo(InfoLevel.maximum),
 retrieveAdditionalJdbcDriverInfo(InfoLevel.maximum),
 retrieveAdditionalTableAttributes(InfoLevel.maximum),
 retrieveColumnDataTypes(InfoLevel.standard),
 retrieveDatabaseInfo(InfoLevel.minimum),
 retrieveForeignKeyDefinitions(InfoLevel.maximum),
 retrieveForeignKeys(InfoLevel.standard),
 retrieveIndexColumnInformation(InfoLevel.maximum),
 retrieveIndexes(InfoLevel.standard),
 retrieveIndexInformation(InfoLevel.maximum),
 retrievePrimaryKeyDefinitions(InfoLevel.maximum),
 retrieveRoutineColumns(InfoLevel.standard),
 retrieveRoutineInformation(InfoLevel.detailed),
 retrieveRoutines(InfoLevel.minimum),
 retrieveSequenceInformation(InfoLevel.maximum),
 retrieveServerInfo(InfoLevel.maximum),
 retrieveSynonymInformation(InfoLevel.maximum),
 retrieveTableColumnPrivileges(InfoLevel.maximum),
 retrieveTableColumns(InfoLevel.standard),
 retrieveTableConstraintDefinitions(InfoLevel.detailed),
 retrieveTableConstraintInformation(InfoLevel.detailed),
 retrieveTableDefinitionsInformation(InfoLevel.maximum),
 retrieveTablePrivileges(InfoLevel.maximum),
 retrieveTables(InfoLevel.minimum),
 retrieveTriggerInformation(InfoLevel.detailed),
 retrieveUserDefinedColumnDataTypes(InfoLevel.detailed),
 retrieveViewInformation(InfoLevel.detailed);

  private static final String SC_SCHEMA_INFO_LEVEL = "schemacrawler.schema_info_level.";

  private final InfoLevel infoLevel;

  private SchemaInfoRetrieval(final InfoLevel infoLevel)
  {
    this.infoLevel = infoLevel;
  }

  public InfoLevel getInfoLevel()
  {
    return infoLevel;
  }

  public String getKey()
  {
    return SC_SCHEMA_INFO_LEVEL + toSnakeCase(name());
  }

}
