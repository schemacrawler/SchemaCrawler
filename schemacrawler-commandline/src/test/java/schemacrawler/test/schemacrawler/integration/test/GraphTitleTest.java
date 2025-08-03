/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.schemacrawler.integration.test;

import java.util.EnumSet;
import java.util.stream.Stream;
import schemacrawler.test.AbstractTitleTest;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

public class GraphTitleTest extends AbstractTitleTest {

  @Override
  public Stream<DiagramOutputFormat> outputFormats() {
    return EnumSet.of(DiagramOutputFormat.scdot, DiagramOutputFormat.htmlx).stream();
  }
}
