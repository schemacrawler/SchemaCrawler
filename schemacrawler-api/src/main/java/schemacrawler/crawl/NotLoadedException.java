/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.TableConstraint;

public class NotLoadedException extends UnsupportedOperationException {

  private static final long serialVersionUID = -1745422469189598709L;

  public NotLoadedException(final PartialDatabaseObject databaseObject) {
    super(String.format("Complete metadata has not been loaded for <%s>", databaseObject));
  }

  public NotLoadedException(final TableConstraint constraint) {
    super(
        String.format(
            "Complete metadata has not been loaded for table constraint <%s>", constraint));
  }
}
