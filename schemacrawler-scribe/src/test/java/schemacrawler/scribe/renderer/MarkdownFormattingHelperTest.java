/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.renderer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;
import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaReference;
import schemacrawler.test.utility.crawl.LightTable;

public class MarkdownFormattingHelperTest {

  @Test
  public void encodeFullNameEncodesSchemaAndName() {
    final Schema schema = new SchemaReference("PUBLIC", "BOOKS");
    final LightTable table = new LightTable(schema, "order details");

    assertThat(MarkdownFormattingHelper.encodeFullName(table), is("PUBLIC.BOOKS.order%20details"));
    assertThat(MarkdownFormattingHelper.encodeFullName(null), is(""));
  }

  @Test
  public void escapeMarkdownEscapesSpecialCharactersAndNewlines() {
    final String escaped =
        MarkdownFormattingHelper.escapeMarkdown("a|b\nc*d_(e)f[g]{h}i#j+k-l.m!n>o`p\\q");

    assertThat(
        escaped, is("a\\|b c\\*d\\_\\(e\\)f\\[g\\]\\{h\\}i\\#j\\+k\\-l\\.m\\!n\\>o\\`p\\\\q"));
    assertThat(MarkdownFormattingHelper.escapeMarkdown(null), is(""));
  }

  @Test
  public void sentenceCaseNormalizesMixedCase() {
    assertThat(MarkdownFormattingHelper.sentenceCase("hELLo"), is("Hello"));
    assertThat(MarkdownFormattingHelper.sentenceCase(""), is(""));
  }
}
