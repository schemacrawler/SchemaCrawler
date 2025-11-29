/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility.ioresource;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

public class NonCloseableWriterTest {

  @Test
  public void testClose() throws IOException {
    final StringWriter stringWriter = new StringWriter();
    final NonCloseableWriter nonCloseableWriter = new NonCloseableWriter(stringWriter);

    nonCloseableWriter.write("test");
    nonCloseableWriter.close();

    // Ensure that the writer is flushed but not closed
    assertThat(stringWriter.toString(), is("test"));

    // Try writing again to ensure it is not closed
    nonCloseableWriter.write("123");
    nonCloseableWriter.flush();
    assertThat(stringWriter.toString(), is("test123"));
  }

  @Test
  public void testToString() throws IOException {
    final StringWriter stringWriter = new StringWriter();
    final NonCloseableWriter nonCloseableWriter = new NonCloseableWriter(stringWriter);

    nonCloseableWriter.write("test");
    nonCloseableWriter.close();

    assertThat(nonCloseableWriter.toString(), is("test"));
  }

  @Test
  public void testWrite() throws IOException {
    final StringWriter stringWriter = new StringWriter();
    final NonCloseableWriter nonCloseableWriter = new NonCloseableWriter(stringWriter);

    nonCloseableWriter.write("test");
    nonCloseableWriter.flush();

    assertThat(stringWriter.toString(), is("test"));
  }
}
