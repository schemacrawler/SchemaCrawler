/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.test.utility;

import java.io.Closeable;
import java.io.Flushable;
import java.nio.file.Path;

public interface TestOutputCapture extends Flushable, Closeable {

  String getContents();

  Path getFilePath();
}
