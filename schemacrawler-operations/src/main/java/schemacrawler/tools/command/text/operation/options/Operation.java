/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation.options;

import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.Query;

public sealed interface Operation permits QueryOperation, OperationType {

  /**
   * Operation description.
   *
   * @return Operation description
   */
  String getDescription();

  /**
   * Operation name.
   *
   * @return Operation name
   */
  String getName();

  /**
   * Query, with override from information schema views.
   *
   * @return Query
   */
  Query getQuery(InformationSchemaViews views);

  /**
   * Operation title.
   *
   * @return Operation title
   */
  String getTitle();
}
