/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.lint;

import java.io.Serializable;
import us.fatehi.utility.property.PropertyName;

public interface LinterProvider extends Serializable {

  /**
   * Gets a description of the linter.
   *
   * @return Description of the linter
   */
  default String getDescription() {
    return getPropertyName().getDescription();
  }

  /**
   * Gets the identification of this linter.
   *
   * @return Identification of this linter
   */
  default String getLinterId() {
    return getPropertyName().getName();
  }

  /**
   * Gets the name and description of the linter.
   *
   * @return Name and description of the linter
   */
  PropertyName getPropertyName();

  Linter newLinter(LintCollector lintCollector);
}
