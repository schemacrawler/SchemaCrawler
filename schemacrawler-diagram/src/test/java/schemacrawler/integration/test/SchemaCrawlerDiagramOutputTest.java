/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.integration.test;

import java.util.EnumSet;
import java.util.stream.Stream;
import schemacrawler.test.AbstractSchemaCrawlerOutputTest;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

public class SchemaCrawlerDiagramOutputTest extends AbstractSchemaCrawlerOutputTest {

  @Override
  public Stream<DiagramOutputFormat> outputFormats() {
    return EnumSet.of(DiagramOutputFormat.scdot).stream();
  }
}
