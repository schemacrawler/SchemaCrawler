/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
