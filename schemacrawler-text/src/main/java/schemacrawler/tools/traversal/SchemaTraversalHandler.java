/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.traversal;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;

public interface SchemaTraversalHandler extends TraversalHandler {

  void handle(final ColumnDataType columnDataType);

  /**
   * Provides information on the database schema.
   *
   * @param routine Routine metadata.
   */
  void handle(final Routine routine);

  /**
   * Provides information on the database schema.
   *
   * @param sequence Sequence metadata.
   */
  void handle(final Sequence sequence);

  /**
   * Provides information on the database schema.
   *
   * @param synonym Synonym metadata.
   */
  void handle(final Synonym synonym);

  /**
   * Provides information on the database schema.
   *
   * @param table Table metadata.
   */
  void handle(final Table table);

  void handleColumnDataTypesEnd();

  void handleColumnDataTypesStart();

  void handleInfo(DatabaseInfo databaseInfo);

  void handleInfo(JdbcDriverInfo jdbcDriverInfo);

  void handleInfoEnd();

  void handleInfoStart();

  void handleRoutinesEnd();

  void handleRoutinesStart();

  void handleSequencesEnd();

  void handleSequencesStart();

  void handleSynonymsEnd();

  void handleSynonymsStart();

  void handleTablesEnd();

  void handleTablesStart();
}
