/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.io.Serial;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.TableConstraint;

public class NotLoadedException extends UnsupportedOperationException {

  @Serial private static final long serialVersionUID = -1745422469189598709L;

  public NotLoadedException(final PartialDatabaseObject databaseObject) {
    super("Complete metadata has not been loaded for <%s>".formatted(databaseObject));
  }

  public NotLoadedException(final TableConstraint constraint) {
    super("Complete metadata has not been loaded for table constraint <%s>".formatted(constraint));
  }
}
