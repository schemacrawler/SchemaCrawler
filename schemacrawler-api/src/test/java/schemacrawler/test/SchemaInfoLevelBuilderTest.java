/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaInfoRetrieval;

public class SchemaInfoLevelBuilderTest {

  @Test
  public void setRetrieveAdditionalColumnMetadata() {

    final Map<SchemaInfoRetrieval, BiConsumer<SchemaInfoLevelBuilder, Boolean>> testMap =
        new HashMap<>();

    testMap.put(
        SchemaInfoRetrieval.retrieveAdditionalColumnAttributes,
        SchemaInfoLevelBuilder::setRetrieveAdditionalColumnAttributes);
    testMap.put(
        SchemaInfoRetrieval.retrieveAdditionalColumnMetadata,
        SchemaInfoLevelBuilder::setRetrieveAdditionalColumnMetadata);
    testMap.put(
        SchemaInfoRetrieval.retrieveAdditionalDatabaseInfo,
        SchemaInfoLevelBuilder::setRetrieveAdditionalDatabaseInfo);
    testMap.put(
        SchemaInfoRetrieval.retrieveAdditionalJdbcDriverInfo,
        SchemaInfoLevelBuilder::setRetrieveAdditionalJdbcDriverInfo);
    testMap.put(
        SchemaInfoRetrieval.retrieveAdditionalTableAttributes,
        SchemaInfoLevelBuilder::setRetrieveAdditionalTableAttributes);
    testMap.put(
        SchemaInfoRetrieval.retrieveColumnDataTypes,
        SchemaInfoLevelBuilder::setRetrieveColumnDataTypes);
    testMap.put(
        SchemaInfoRetrieval.retrieveDatabaseInfo, SchemaInfoLevelBuilder::setRetrieveDatabaseInfo);
    testMap.put(
        SchemaInfoRetrieval.retrieveDatabaseUsers,
        SchemaInfoLevelBuilder::setRetrieveDatabaseUsers);
    testMap.put(
        SchemaInfoRetrieval.retrievePrimaryKeys, SchemaInfoLevelBuilder::setRetrievePrimaryKeys);
    testMap.put(
        SchemaInfoRetrieval.retrieveForeignKeys, SchemaInfoLevelBuilder::setRetrieveForeignKeys);
    testMap.put(SchemaInfoRetrieval.retrieveIndexes, SchemaInfoLevelBuilder::setRetrieveIndexes);
    testMap.put(
        SchemaInfoRetrieval.retrieveIndexInformation,
        SchemaInfoLevelBuilder::setRetrieveIndexInformation);
    testMap.put(
        SchemaInfoRetrieval.retrieveRoutineInformation,
        SchemaInfoLevelBuilder::setRetrieveRoutineInformation);
    testMap.put(
        SchemaInfoRetrieval.retrieveRoutineParameters,
        SchemaInfoLevelBuilder::setRetrieveRoutineParameters);
    testMap.put(SchemaInfoRetrieval.retrieveRoutines, SchemaInfoLevelBuilder::setRetrieveRoutines);
    testMap.put(
        SchemaInfoRetrieval.retrieveSequenceInformation,
        SchemaInfoLevelBuilder::setRetrieveSequenceInformation);
    testMap.put(
        SchemaInfoRetrieval.retrieveServerInfo, SchemaInfoLevelBuilder::setRetrieveServerInfo);
    testMap.put(
        SchemaInfoRetrieval.retrieveSynonymInformation,
        SchemaInfoLevelBuilder::setRetrieveSynonymInformation);
    testMap.put(
        SchemaInfoRetrieval.retrieveTableColumnPrivileges,
        SchemaInfoLevelBuilder::setRetrieveTableColumnPrivileges);
    testMap.put(
        SchemaInfoRetrieval.retrieveTableColumns, SchemaInfoLevelBuilder::setRetrieveTableColumns);
    testMap.put(
        SchemaInfoRetrieval.retrieveTableConstraintDefinitions,
        SchemaInfoLevelBuilder::setRetrieveTableConstraintDefinitions);
    testMap.put(
        SchemaInfoRetrieval.retrieveTableConstraintInformation,
        SchemaInfoLevelBuilder::setRetrieveTableConstraintInformation);
    testMap.put(
        SchemaInfoRetrieval.retrieveTableDefinitionsInformation,
        SchemaInfoLevelBuilder::setRetrieveTableDefinitionsInformation);
    testMap.put(
        SchemaInfoRetrieval.retrieveTablePrivileges,
        SchemaInfoLevelBuilder::setRetrieveTablePrivileges);
    testMap.put(SchemaInfoRetrieval.retrieveTables, SchemaInfoLevelBuilder::setRetrieveTables);
    testMap.put(
        SchemaInfoRetrieval.retrieveTriggerInformation,
        SchemaInfoLevelBuilder::setRetrieveTriggerInformation);
    testMap.put(
        SchemaInfoRetrieval.retrieveUserDefinedColumnDataTypes,
        SchemaInfoLevelBuilder::setRetrieveUserDefinedColumnDataTypes);
    testMap.put(
        SchemaInfoRetrieval.retrieveViewInformation,
        SchemaInfoLevelBuilder::setRetrieveViewInformation);
    testMap.put(
        SchemaInfoRetrieval.retrieveViewTableUsage,
        SchemaInfoLevelBuilder::setRetrieveViewViewTableUsage);

    final SchemaInfoLevelBuilder builder = SchemaInfoLevelBuilder.builder();

    testMap.forEach(
        (infoRetrieval, consumer) -> {
          consumer.accept(builder, true);
          assertThat(
              "Failed for " + infoRetrieval, builder.toOptions().is(infoRetrieval), is(true));

          consumer.accept(builder, false);
          assertThat(
              "Failed for " + infoRetrieval, builder.toOptions().is(infoRetrieval), is(false));
        });
  }

  @Test
  public void testFromOptions() {
    final SchemaInfoLevel options1 = SchemaInfoLevelBuilder.standard();
    final SchemaInfoLevel options2 =
        SchemaInfoLevelBuilder.builder().fromOptions(options1).toOptions();
    assertThat(options1, equalTo(options2));
  }
}
