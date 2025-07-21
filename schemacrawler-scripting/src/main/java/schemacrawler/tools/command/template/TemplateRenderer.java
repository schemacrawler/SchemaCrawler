/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.template;

import java.util.Map;
import schemacrawler.tools.options.OutputOptions;


public interface TemplateRenderer {

  void execute();

  void setContext(Map<String, Object> context);

  void setOutputOptions(OutputOptions outputOptions);

  void setResourceFilename(String resourceFilename);
}
