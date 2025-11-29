/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import static java.util.Objects.requireNonNull;
import static schemacrawler.schemacrawler.DatabaseObjectInfoRetrieval.base;
import static schemacrawler.schemacrawler.DatabaseObjectInfoRetrieval.database;
import static schemacrawler.schemacrawler.DatabaseObjectInfoRetrieval.other;
import static schemacrawler.schemacrawler.DatabaseObjectInfoRetrieval.routine;
import static schemacrawler.schemacrawler.DatabaseObjectInfoRetrieval.table;
import static schemacrawler.schemacrawler.InfoLevel.detailed;
import static schemacrawler.schemacrawler.InfoLevel.maximum;
import static schemacrawler.schemacrawler.InfoLevel.minimum;
import static schemacrawler.schemacrawler.InfoLevel.standard;

public enum SchemaInfoRetrieval {
  retrieveAdditionalColumnAttributes(table, maximum),
  retrieveAdditionalColumnMetadata(table, maximum),
  retrieveAdditionalDatabaseInfo(database, maximum),
  retrieveAdditionalJdbcDriverInfo(database, maximum),
  retrieveAdditionalTableAttributes(table, maximum),
  retrieveColumnDataTypes(base, standard),
  retrieveDatabaseInfo(database, minimum),
  retrieveDatabaseUsers(database, maximum),
  retrieveForeignKeys(table, standard),
  retrieveIndexes(table, standard),
  retrieveIndexInformation(table, maximum),
  retrievePrimaryKeys(table, standard),
  retrieveRoutineParameters(routine, standard),
  retrieveRoutineInformation(routine, detailed),
  retrieveRoutineReferences(routine, detailed),
  retrieveRoutines(routine, minimum),
  retrieveSequenceInformation(other, maximum),
  retrieveServerInfo(database, maximum),
  retrieveSynonymInformation(other, maximum),
  retrieveTableColumnPrivileges(table, maximum),
  retrieveTableColumns(table, standard),
  retrieveTableConstraints(table, detailed),
  retrieveTableConstraintColumns(table, detailed),
  retrieveTableConstraintDefinitions(table, detailed),
  retrieveTableConstraintInformation(table, detailed),
  retrieveTableDefinitionsInformation(table, maximum),
  retrieveTablePrivileges(table, maximum),
  retrieveTables(table, minimum),
  retrieveTriggerInformation(table, detailed),
  retrieveUserDefinedColumnDataTypes(other, detailed),
  retrieveViewInformation(table, detailed),
  retrieveViewTableUsage(table, detailed),
  ;

  private final DatabaseObjectInfoRetrieval databaseObjectInfoRetrieval;
  private final InfoLevel infoLevel;

  SchemaInfoRetrieval(
      final DatabaseObjectInfoRetrieval databaseObjectInfoRetrieval, final InfoLevel infoLevel) {
    this.infoLevel = requireNonNull(infoLevel);
    this.databaseObjectInfoRetrieval = requireNonNull(databaseObjectInfoRetrieval);
  }

  public DatabaseObjectInfoRetrieval getDatabaseObjectInfoRetrieval() {
    return databaseObjectInfoRetrieval;
  }

  public InfoLevel getInfoLevel() {
    return infoLevel;
  }
}
