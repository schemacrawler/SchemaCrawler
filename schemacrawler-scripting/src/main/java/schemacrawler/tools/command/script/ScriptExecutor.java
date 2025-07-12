/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

public interface ScriptExecutor extends Runnable {

  boolean canGenerate();

  void initialize(Map<String, Object> context, Reader reader, Writer writer);
}
