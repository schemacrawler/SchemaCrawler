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


public final class SchemaInfoLevelBuilder
// implements OptionsBuilder<SchemaInfoLevel>
{

  /**
   * Creates a new SchemaInfoLevel for verbose schema information.
   *
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel detailed()
  {
    final SchemaInfoLevel detailed = standard();
    detailed.setRetrieveUserDefinedColumnDataTypes(true);
    detailed.setRetrieveTriggerInformation(true);
    detailed.setRetrieveTableConstraintInformation(true);
    detailed.setRetrieveTableDefinitionsInformation(true);
    detailed.setRetrieveIndexInformation(true);
    detailed.setRetrieveViewInformation(true);
    detailed.setRetrieveRoutineInformation(true);
    detailed.setTag("detailed");
    return detailed;
  }

  /**
   * Creates a new SchemaInfoLevel for maximum schema information.
   *
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel maximum()
  {
    final SchemaInfoLevel maximum = detailed();
    maximum.setRetrieveAdditionalDatabaseInfo(true);
    maximum.setRetrieveAdditionalJdbcDriverInfo(true);
    maximum.setRetrieveTablePrivileges(true);
    maximum.setRetrieveTableColumnPrivileges(true);
    maximum.setRetrieveHiddenTableColumns(true);
    maximum.setRetrieveAdditionalTableAttributes(true);
    maximum.setRetrieveAdditionalColumnAttributes(true);
    maximum.setRetrieveSequenceInformation(true);
    maximum.setRetrieveSynonymInformation(true);
    maximum.setTag("maximum");
    return maximum;
  }

  /**
   * Creates a new SchemaInfoLevel for minimum schema information.
   *
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel minimum()
  {
    final SchemaInfoLevel minimum = new SchemaInfoLevel();
    minimum.setRetrieveTables(true);
    minimum.setRetrieveRoutines(true);
    minimum.setTag("minimum");
    return minimum;
  }

  /**
   * Creates a new SchemaInfoLevel for standard schema information.
   *
   * @return New SchemaInfoLevel
   */
  public static SchemaInfoLevel standard()
  {
    final SchemaInfoLevel standard = minimum();
    standard.setRetrieveColumnDataTypes(true);
    standard.setRetrieveTableColumns(true);
    standard.setRetrieveForeignKeys(true);
    standard.setRetrieveIndexes(true);
    standard.setRetrieveRoutineColumns(true);
    standard.setTag("standard");
    return standard;
  }

}
