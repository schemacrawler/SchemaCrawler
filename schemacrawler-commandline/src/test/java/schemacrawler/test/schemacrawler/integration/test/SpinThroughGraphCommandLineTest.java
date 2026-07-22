/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.schemacrawler.integration.test;

import java.util.EnumSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.Tag;
import schemacrawler.test.AbstractSpinThroughCommandLineTest;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

@Tag("graphviz")
public class SpinThroughGraphCommandLineTest extends AbstractSpinThroughCommandLineTest {

  @Override
  public Stream<DiagramOutputFormat> outputFormats() {
    return EnumSet.of(DiagramOutputFormat.scdot, DiagramOutputFormat.htmlx).stream();
  }
}
