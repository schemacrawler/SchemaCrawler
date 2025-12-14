/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.text.formatter.operation;

import java.sql.ResultSet;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Query;
import schemacrawler.tools.traversal.TraversalHandler;

public interface DataTraversalHandler extends TraversalHandler {

  void handleData(final Query query, final ResultSet rows);

  void handleData(final Table table, final ResultSet rows);
}
