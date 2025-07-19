/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.operation.options;

import schemacrawler.schemacrawler.Query;

public interface Operation {

  /**
   * Operation description.
   *
   * @return Operation description
   */
  String getDescription();

  /**
   * Query.
   *
   * @return Query
   */
  Query getQuery();

  /**
   * Operation title.
   *
   * @return Operation title
   */
  String getTitle();
}
