/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.lint;

import java.io.Serial;
import us.fatehi.utility.property.PropertyName;

/**
 * Defines a linter, and allows the creation of a new one, since multiple instances of the same
 * linter can be created.
 */
public abstract class BaseLinterProvider implements LinterProvider {

  @Serial private static final long serialVersionUID = -7188840789229724389L;

  private final PropertyName linterName;

  public BaseLinterProvider(final String linterId) {
    linterName = new PropertyName(linterId, LintUtility.readDescription(linterId));
  }

  /**
   * @{inheritDoc}
   */
  @Override
  public PropertyName getPropertyName() {
    return linterName;
  }
}
