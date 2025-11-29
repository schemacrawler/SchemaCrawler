/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class SchemaInfoLevelBuilder
    implements OptionsBuilder<SchemaInfoLevelBuilder, SchemaInfoLevel> {

  private static final Logger LOGGER = Logger.getLogger(SchemaInfoLevelBuilder.class.getName());

  public static SchemaInfoLevelBuilder builder() {
    return new SchemaInfoLevelBuilder();
  }

  /**
   * Creates a new SchemaInfoLevel with settings for detailed schema information.
   *
   * @return SchemaInfoLevel detailed
   */
  public static SchemaInfoLevel detailed() {
    return builder().withTag("detailed").withInfoLevel(InfoLevel.detailed).toOptions();
  }

  /**
   * Creates a new SchemaInfoLevel with settings for maximum schema information.
   *
   * @return SchemaInfoLevel maximum
   */
  public static SchemaInfoLevel maximum() {
    return builder().withTag("maximum").withInfoLevel(InfoLevel.maximum).toOptions();
  }

  /**
   * Creates a new SchemaInfoLevel with settings for minimum schema information.
   *
   * @return SchemaInfoLevel minimum
   */
  public static SchemaInfoLevel minimum() {
    return builder().withTag("minimum").withInfoLevel(InfoLevel.minimum).toOptions();
  }

  /**
   * Retrieves schema based on standard options.
   *
   * @return Standard schema info level.
   */
  public static SchemaInfoLevel newSchemaInfoLevel() {
    return standard();
  }

  /**
   * Creates a new SchemaInfoLevel with settings for standard schema information.
   *
   * @return SchemaInfoLevel standard
   */
  public static SchemaInfoLevel standard() {
    return builder().withTag("standard").withInfoLevel(InfoLevel.standard).toOptions();
  }

  private final Map<SchemaInfoRetrieval, Boolean> schemaInfoRetrievals;
  private String tag;

  private SchemaInfoLevelBuilder() {
    tag = "";
    // Retrieve nothing
    schemaInfoRetrievals = new EnumMap<>(SchemaInfoRetrieval.class);
  }

  @Override
  public SchemaInfoLevelBuilder fromOptions(final SchemaInfoLevel schemaInfoLevel) {
    if (schemaInfoLevel == null) {
      return this;
    }

    tag = schemaInfoLevel.getTag();

    try {
      for (final SchemaInfoRetrieval schemaInfoRetrieval : SchemaInfoRetrieval.values()) {
        final boolean booleanValue = schemaInfoLevel.is(schemaInfoRetrieval);
        schemaInfoRetrievals.put(schemaInfoRetrieval, booleanValue);
      }
    } catch (final Exception e) {
      LOGGER.log(Level.WARNING, "Could not obtain schema info level settings", e);
    }

    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalColumnAttributes(
      final boolean retrieveAdditionalColumnAttributes) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveAdditionalColumnAttributes, retrieveAdditionalColumnAttributes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalColumnMetadata(
      final boolean retrieveAdditionalColumnMetadata) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveAdditionalColumnMetadata, retrieveAdditionalColumnMetadata);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalDatabaseInfo(
      final boolean retrieveAdditionalDatabaseInfo) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveAdditionalDatabaseInfo, retrieveAdditionalDatabaseInfo);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalJdbcDriverInfo(
      final boolean retrieveAdditionalJdbcDriverInfo) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveAdditionalJdbcDriverInfo, retrieveAdditionalJdbcDriverInfo);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveAdditionalTableAttributes(
      final boolean retrieveAdditionalTableAttributes) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveAdditionalTableAttributes, retrieveAdditionalTableAttributes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveColumnDataTypes(final boolean retrieveColumnDataTypes) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveColumnDataTypes, retrieveColumnDataTypes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveDatabaseInfo(final boolean retrieveDatabaseInfo) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveDatabaseInfo, retrieveDatabaseInfo);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveDatabaseUsers(final boolean retrieveDatabaseUsers) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveDatabaseUsers, retrieveDatabaseUsers);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveForeignKeys(final boolean retrieveForeignKeys) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveForeignKeys, retrieveForeignKeys);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveIndexes(final boolean retrieveIndexes) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveIndexes, retrieveIndexes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveIndexInformation(
      final boolean retrieveIndexInformation) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveIndexInformation, retrieveIndexInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrievePrimaryKeys(final boolean retrievePrimaryKeys) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrievePrimaryKeys, retrievePrimaryKeys);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutineInformation(
      final boolean retrieveRoutineInformation) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveRoutineInformation, retrieveRoutineInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutineParameters(
      final boolean retrieveRoutineParameters) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveRoutineParameters, retrieveRoutineParameters);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveRoutines(final boolean retrieveRoutines) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveRoutines, retrieveRoutines);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveSequenceInformation(
      final boolean retrieveSequenceInformation) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveSequenceInformation, retrieveSequenceInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveServerInfo(final boolean retrieveServerInfo) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveServerInfo, retrieveServerInfo);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveSynonymInformation(
      final boolean retrieveSynonymInformation) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveSynonymInformation, retrieveSynonymInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableColumnPrivileges(
      final boolean retrieveTableColumnPrivileges) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveTableColumnPrivileges, retrieveTableColumnPrivileges);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableColumns(final boolean retrieveTableColumns) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveTableColumns, retrieveTableColumns);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableConstraintDefinitions(
      final boolean retrieveTableConstraintDefinitions) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveTableConstraintDefinitions, retrieveTableConstraintDefinitions);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableConstraintInformation(
      final boolean retrieveTableConstraintInformation) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveTableConstraintInformation, retrieveTableConstraintInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableConstraints(
      final boolean retrieveTableConstraints) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveTableConstraints, retrieveTableConstraints);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTableDefinitionsInformation(
      final boolean retrieveTableDefinitionsInformation) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveTableDefinitionsInformation,
        retrieveTableDefinitionsInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTablePrivileges(final boolean retrieveTablePrivileges) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveTablePrivileges, retrieveTablePrivileges);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTables(final boolean retrieveTables) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveTables, retrieveTables);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveTriggerInformation(
      final boolean retrieveTriggerInformation) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveTriggerInformation, retrieveTriggerInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveUserDefinedColumnDataTypes(
      final boolean retrieveUserDefinedColumnDataTypes) {
    schemaInfoRetrievals.put(
        SchemaInfoRetrieval.retrieveUserDefinedColumnDataTypes, retrieveUserDefinedColumnDataTypes);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveViewInformation(final boolean retrieveViewInformation) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveViewInformation, retrieveViewInformation);
    return this;
  }

  public SchemaInfoLevelBuilder setRetrieveViewViewTableUsage(
      final boolean retrieveViewTableUsage) {
    schemaInfoRetrievals.put(SchemaInfoRetrieval.retrieveViewTableUsage, retrieveViewTableUsage);
    return this;
  }

  @Override
  public SchemaInfoLevel toOptions() {
    reduceMap();
    return new SchemaInfoLevel(tag, schemaInfoRetrievals);
  }

  @Override
  public String toString() {
    return tag;
  }

  /**
   * Updates SchemaInfoLevel builder with settings for a given info level.
   *
   * @return SchemaInfoLevel builder
   */
  public SchemaInfoLevelBuilder withInfoLevel(final InfoLevel infoLevel) {
    if (infoLevel == null) {
      return this;
    }
    final int infoLevelOrdinal = infoLevel.ordinal();
    for (final SchemaInfoRetrieval schemaInfoRetrieval : SchemaInfoRetrieval.values()) {
      final int schemaInfoLevelOrdinal = schemaInfoRetrieval.getInfoLevel().ordinal();
      if (schemaInfoLevelOrdinal <= infoLevelOrdinal) {
        schemaInfoRetrievals.put(schemaInfoRetrieval, true);
      }
    }

    if (isBlank(tag)) {
      withTag(infoLevel.name());
    }

    return this;
  }

  /**
   * Updates SchemaInfoLevel builder by removing settings to retrieve routines.
   *
   * @return SchemaInfoLevel builder
   */
  public SchemaInfoLevelBuilder withoutRoutines() {
    return withoutDatabaseObjectInfoRetrieval(DatabaseObjectInfoRetrieval.routine);
  }

  /**
   * Updates SchemaInfoLevel builder by removing settings to retrieve tables.
   *
   * @return SchemaInfoLevel builder
   */
  public SchemaInfoLevelBuilder withoutTables() {
    return withoutDatabaseObjectInfoRetrieval(DatabaseObjectInfoRetrieval.table);
  }

  public SchemaInfoLevelBuilder withTag(final String tag) {
    this.tag = trimToEmpty(tag);
    return this;
  }

  private void reduceMap() {
    for (final SchemaInfoRetrieval schemaInfoRetrieval : SchemaInfoRetrieval.values()) {
      if (!schemaInfoRetrievals.getOrDefault(schemaInfoRetrieval, false)) {
        schemaInfoRetrievals.remove(schemaInfoRetrieval);
      }
    }
  }

  private SchemaInfoLevelBuilder withoutDatabaseObjectInfoRetrieval(
      final DatabaseObjectInfoRetrieval databaseObjectInfoRetrieval) {
    for (final SchemaInfoRetrieval schemaInfoRetrieval : SchemaInfoRetrieval.values()) {
      if (schemaInfoRetrieval.getDatabaseObjectInfoRetrieval() == databaseObjectInfoRetrieval) {
        schemaInfoRetrievals.remove(schemaInfoRetrieval);
      }
    }

    return this;
  }
}
