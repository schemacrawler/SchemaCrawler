/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.options;

import java.io.Serializable;
import java.util.List;

public interface OutputFormat extends Serializable {

  String getDescription();

  String getFormat();

  List<String> getFormats();
}
