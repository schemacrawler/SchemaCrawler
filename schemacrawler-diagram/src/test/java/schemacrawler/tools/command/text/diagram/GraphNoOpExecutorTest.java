/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.diagram;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.ExecutionRuntimeException;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

public class GraphNoOpExecutorTest {

  @Test
  public void canGenerate() {
    assertThat(new GraphNoOpExecutor(DiagramOutputFormat.scdot).canGenerate(), is(true));
  }

  @Test
  public void constructor() {
    assertThrows(NullPointerException.class, () -> new GraphNoOpExecutor(null));
    assertThrows(
        ExecutionRuntimeException.class, () -> new GraphNoOpExecutor(DiagramOutputFormat.bmp));
  }
}
