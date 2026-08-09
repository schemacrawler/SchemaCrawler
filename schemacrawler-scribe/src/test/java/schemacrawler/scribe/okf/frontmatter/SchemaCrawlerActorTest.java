/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.startsWith;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.Version;
import schemacrawler.scribe.okf.frontmatter.okf.SchemaCrawlerActor;

public class SchemaCrawlerActorTest {

  @Test
  public void schemaCrawlerActorUsesVersionedPrefix() {
    final String actor = new SchemaCrawlerActor().toString();
    assertThat(actor, startsWith("process:schemacrawler-v"));
    assertThat(actor, endsWith(Version.version().getProductVersion()));
  }
}
