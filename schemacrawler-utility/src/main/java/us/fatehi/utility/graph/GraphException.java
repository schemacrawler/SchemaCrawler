/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.graph;

import java.io.Serial;

public class GraphException extends Exception {

  @Serial private static final long serialVersionUID = 5978689857777212149L;

  public GraphException(final String message) {
    super(message);
  }
}
