/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import java.util.EnumSet;
import java.util.stream.Stream;
import schemacrawler.tools.command.text.schema.options.TextOutputFormat;

public class TitleTest extends AbstractTitleTest {

  @Override
  public Stream<TextOutputFormat> outputFormats() {
    return EnumSet.of(TextOutputFormat.text, TextOutputFormat.html).stream();
  }
}
