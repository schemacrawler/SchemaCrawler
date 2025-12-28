/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptionsBuilder;
import schemacrawler.tools.text.options.DatabaseObjectColorMap;
import us.fatehi.utility.Color;

public class DatabaseObjectColorMapTest {

  @Test
  public void generateColors() {
    final DatabaseObjectColorMap colorMap =
        SchemaTextOptionsBuilder.builder().toOptions().getColorMap();

    Color color;

    assertThrows(NullPointerException.class, () -> colorMap.getColor(null));

    color = colorMap.getColor(new LightTable(new SchemaReference(null, null), "table"));
    assertThat(color, is(Color.fromRGB(0xF2, 0xE6, 0xC2)));

    color = colorMap.getColor(new LightTable(new SchemaReference(null, "schema"), "table"));
    assertThat(color, is(Color.fromRGB(0xF2, 0xDD, 0xC2)));

    color = colorMap.getColor(new LightTable(new SchemaReference("catalog", "schema"), "table"));
    assertThat(color, is(Color.fromRGB(0xF2, 0xD9, 0xC2)));

    color = colorMap.getColor(new LightTable(new SchemaReference("catalog", null), "table"));
    assertThat(color, is(Color.fromRGB(0xC2, 0xF2, 0xEC)));
  }
}
