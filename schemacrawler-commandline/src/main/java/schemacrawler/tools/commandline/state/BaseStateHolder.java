/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.commandline.state;

import static java.util.Objects.requireNonNull;

public abstract class BaseStateHolder {

  protected final ShellState state;

  protected BaseStateHolder(final ShellState state) {
    this.state = requireNonNull(state, "No shell state provided");
  }
}
