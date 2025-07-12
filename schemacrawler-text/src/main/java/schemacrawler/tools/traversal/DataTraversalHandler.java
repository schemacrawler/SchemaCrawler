/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.traversal;

import java.sql.ResultSet;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Query;

public interface DataTraversalHandler extends TraversalHandler {

  void handleData(final Query query, final ResultSet rows);

  void handleData(final Table table, final ResultSet rows);
}
