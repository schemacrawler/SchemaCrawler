/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static java.lang.System.lineSeparator;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.InfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import schemacrawler.schemacrawler.SchemaInfoRetrieval;

public class SchemaInfoLevelBuilderTest {

  @Test
  public void nullInfoLevel() {
    assertThat(
        SchemaInfoLevelBuilder.builder().withInfoLevel(null).toOptions(),
        is(SchemaInfoLevelBuilder.builder().toOptions()));
  }

  @Test
  public void prebuilt() {
    final SchemaInfoLevel minimum = SchemaInfoLevelBuilder.minimum();
    assertThat(minimum.getTag(), is("minimum"));
    assertThat(minimum.is(SchemaInfoRetrieval.retrieveTables), is(true));
    assertThat(minimum.is(SchemaInfoRetrieval.retrievePrimaryKeys), is(false));
    assertThat(minimum.is(SchemaInfoRetrieval.retrieveRoutineInformation), is(false));
    assertThat(minimum.is(SchemaInfoRetrieval.retrieveAdditionalColumnAttributes), is(false));

    final SchemaInfoLevel standard = SchemaInfoLevelBuilder.standard();
    assertThat(standard.getTag(), is("standard"));
    assertThat(standard.is(SchemaInfoRetrieval.retrieveTables), is(true));
    assertThat(standard.is(SchemaInfoRetrieval.retrievePrimaryKeys), is(true));
    assertThat(standard.is(SchemaInfoRetrieval.retrieveRoutineInformation), is(false));
    assertThat(standard.is(SchemaInfoRetrieval.retrieveAdditionalColumnAttributes), is(false));

    final SchemaInfoLevel detailed = SchemaInfoLevelBuilder.detailed();
    assertThat(detailed.getTag(), is("detailed"));
    assertThat(detailed.is(SchemaInfoRetrieval.retrieveTables), is(true));
    assertThat(detailed.is(SchemaInfoRetrieval.retrievePrimaryKeys), is(true));
    assertThat(detailed.is(SchemaInfoRetrieval.retrieveRoutineInformation), is(true));
    assertThat(detailed.is(SchemaInfoRetrieval.retrieveAdditionalColumnAttributes), is(false));

    final SchemaInfoLevel maximum = SchemaInfoLevelBuilder.maximum();
    assertThat(maximum.getTag(), is("maximum"));
    assertThat(maximum.is(SchemaInfoRetrieval.retrieveTables), is(true));
    assertThat(maximum.is(SchemaInfoRetrieval.retrievePrimaryKeys), is(true));
    assertThat(maximum.is(SchemaInfoRetrieval.retrieveRoutineInformation), is(true));
    assertThat(maximum.is(SchemaInfoRetrieval.retrieveAdditionalColumnAttributes), is(true));

    assertThat(SchemaInfoLevelBuilder.newSchemaInfoLevel(), is(standard));
    assertThat(standard.is(null), is(false));
  }

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
        SchemaInfoRetrieval.retrieveTableConstraints,
        SchemaInfoLevelBuilder::setRetrieveTableConstraints);
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
  public void tag() {
    final SchemaInfoLevelBuilder builder = SchemaInfoLevelBuilder.builder();
    SchemaInfoLevel options;

    assertThat(builder.toString(), equalTo(""));

    options = builder.toOptions();
    assertThat(builder.toString(), equalTo(options.getTag()));
    assertThat(options.getTag(), equalTo(""));
    assertThat(options.toString().replaceAll(lineSeparator(), "\n"), startsWith("{"));

    builder.withInfoLevel(InfoLevel.standard);
    options = builder.toOptions();
    assertThat(builder.toString(), equalTo(options.getTag()));
    assertThat(options.getTag(), equalTo("standard"));
    assertThat(
        options.toString().replaceAll(lineSeparator(), ""),
        is(
            "{  \"retrieveAdditionalColumnAttributes\": false,  \"retrieveAdditionalColumnMetadata\": false,  "
                + "\"retrieveAdditionalDatabaseInfo\": false,  \"retrieveAdditionalJdbcDriverInfo\": false,  "
                + "\"retrieveAdditionalTableAttributes\": false,  \"retrieveColumnDataTypes\": true,  "
                + "\"retrieveDatabaseInfo\": true,  \"retrieveDatabaseUsers\": false,  \"retrieveForeignKeys\": true,  "
                + "\"retrieveIndexInformation\": false,  \"retrieveIndexes\": true,  \"retrievePrimaryKeys\": true,  "
                + "\"retrieveRoutineInformation\": false,  \"retrieveRoutineParameters\": true,  \"retrieveRoutines\": true,  "
                + "\"retrieveSequenceInformation\": false,  \"retrieveServerInfo\": false,  \"retrieveSynonymInformation\": false,  "
                + "\"retrieveTableColumnPrivileges\": false,  \"retrieveTableColumns\": true,  "
                + "\"retrieveTableConstraintDefinitions\": false,  \"retrieveTableConstraintInformation\": false,  \"retrieveTableConstraints\": false,  "
                + "\"retrieveTableDefinitionsInformation\": false,  \"retrieveTablePrivileges\": false,  \"retrieveTables\": true,  "
                + "\"retrieveTriggerInformation\": false,  \"retrieveUserDefinedColumnDataTypes\": false,  "
                + "\"retrieveViewInformation\": false,  \"retrieveViewTableUsage\": false}"));

    builder.withTag("custom");
    options = builder.toOptions();
    assertThat(builder.toString(), equalTo(options.getTag()));
    assertThat(options.getTag(), equalTo("custom"));
    assertThat(
        options.toString().replaceAll(lineSeparator(), ""),
        is(
            "{  \"retrieveAdditionalColumnAttributes\": false,  \"retrieveAdditionalColumnMetadata\": false,  "
                + "\"retrieveAdditionalDatabaseInfo\": false,  \"retrieveAdditionalJdbcDriverInfo\": false,  "
                + "\"retrieveAdditionalTableAttributes\": false,  \"retrieveColumnDataTypes\": true,  \"retrieveDatabaseInfo\": true,  "
                + "\"retrieveDatabaseUsers\": false,  \"retrieveForeignKeys\": true,  \"retrieveIndexInformation\": false,  "
                + "\"retrieveIndexes\": true,  \"retrievePrimaryKeys\": true,  \"retrieveRoutineInformation\": false,  "
                + "\"retrieveRoutineParameters\": true,  \"retrieveRoutines\": true,  \"retrieveSequenceInformation\": false,  "
                + "\"retrieveServerInfo\": false,  \"retrieveSynonymInformation\": false,  \"retrieveTableColumnPrivileges\": false,  "
                + "\"retrieveTableColumns\": true,  "
                + "\"retrieveTableConstraintDefinitions\": false,  \"retrieveTableConstraintInformation\": false,  \"retrieveTableConstraints\": false,  "
                + "\"retrieveTableDefinitionsInformation\": false,  "
                + "\"retrieveTablePrivileges\": false,  \"retrieveTables\": true,  \"retrieveTriggerInformation\": false,  "
                + "\"retrieveUserDefinedColumnDataTypes\": false,  \"retrieveViewInformation\": false,  \"retrieveViewTableUsage\": false}"));

    builder.withTag("\t\t");
    options = builder.toOptions();
    assertThat(builder.toString(), equalTo(options.getTag()));
    assertThat(options.getTag(), equalTo(""));
  }

  @Test
  public void testFromOptions() {
    final SchemaInfoLevel options0 = SchemaInfoLevelBuilder.builder().toOptions();
    final SchemaInfoLevel options1 = SchemaInfoLevelBuilder.standard();
    final SchemaInfoLevel options2 =
        SchemaInfoLevelBuilder.builder().fromOptions(options1).toOptions();
    final SchemaInfoLevel options3 = SchemaInfoLevelBuilder.builder().fromOptions(null).toOptions();
    final SchemaInfoLevel options4 = SchemaInfoLevelBuilder.builder().withTag("custom").toOptions();

    assertThat(options0, not(equalTo(options1)));
    assertThat(options0, equalTo(options3));
    assertThat(options0, not(equalTo(options4)));
    assertThat(options1, equalTo(options2));
    assertThat(options1, not(equalTo(options4)));
  }

  @Test
  public void without() {
    final SchemaInfoLevelBuilder builder =
        SchemaInfoLevelBuilder.builder().withInfoLevel(InfoLevel.standard);
    SchemaInfoLevel options;

    options = builder.toOptions();
    assertThat(options.is(SchemaInfoRetrieval.retrieveTables), equalTo(true));
    assertThat(options.is(SchemaInfoRetrieval.retrieveRoutines), equalTo(true));

    builder.withoutTables().withoutRoutines();
    options = builder.toOptions();
    assertThat(options.is(SchemaInfoRetrieval.retrieveTables), equalTo(false));
    assertThat(options.is(SchemaInfoRetrieval.retrieveRoutines), equalTo(false));
  }
}
