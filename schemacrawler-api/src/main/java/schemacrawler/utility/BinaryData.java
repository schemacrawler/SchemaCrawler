/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.utility;

import java.io.Serial;
import java.io.Serializable;

/** Placeholder for binary column data that was not retrieved from the database. */
public final class BinaryData implements Serializable {

  /** */
  @Serial private static final long serialVersionUID = -6452958466731992721L;

  @Override
  public String toString() {
    return "<binary>";
  }
}
