/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static schemacrawler.scribe.renderer.JsonUtility.yamlMapper;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import schemacrawler.scribe.okf.frontmatter.CountsRecord;
import schemacrawler.scribe.okf.frontmatter.GitHubPagesFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerActor;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.okf.GeneratedRecord;
import schemacrawler.scribe.okf.frontmatter.okf.LifecycleStatus;
import schemacrawler.scribe.okf.frontmatter.okf.OkfFrontMatterRecord;
import schemacrawler.scribe.okf.frontmatter.okf.TrustTier;
import schemacrawler.scribe.okf.frontmatter.okf.VerifiedRecord;
import tools.jackson.databind.JsonNode;

public class OkfFrontMatterYamlUtilityTest {

  @Test
  public void mergesFrontMatterAndOmitsOptionalFields() throws Exception {
    final OkfFrontMatterYamlUtility utility = new OkfFrontMatterYamlUtility();
    final SchemaCrawlerActor actor = new SchemaCrawlerActor();
    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            "table",
            "PUBLIC.BOOKS",
            "Books table",
            null,
            List.of("table", "has_triggers"),
            new GeneratedRecord(actor, Instant.parse("2026-01-01T00:00:00Z")),
            new VerifiedRecord(TrustTier.machine_confirmed, actor),
            LifecycleStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord("books", "Books table", true, true);
    final SchemaCrawlerFrontMatterRecord schemaCrawlerFrontMatter =
        new SchemaCrawlerFrontMatterRecord(
            "PUBLIC.BOOKS", "BOOKS", null, new CountsRecord(2, 0, 1, 1, null, null), null);

    final String yaml =
        utility.toYamlString(okfFrontMatter, gitHubPagesFrontMatter, schemaCrawlerFrontMatter);
    final JsonNode parsed = yamlMapper.readTree(yaml);

    assertThat(yamlMapper.treeToValue(parsed.get("type"), String.class), is("table"));
    assertThat(parsed.get("resource"), is(nullValue()));
    assertThat(yamlMapper.treeToValue(parsed.get("tags").get(1), String.class), is("has_triggers"));
    assertThat(parsed.get("generated"), is(notNullValue()));
    assertThat(parsed.get("verified"), is(notNullValue()));
    assertThat(parsed.get("counts"), is(notNullValue()));
    assertThat(parsed.get("counts").get("row_count"), is(nullValue()));
    assertThat(parsed.get("complete_type"), is(nullValue()));
    assertThat(parsed.get("entity_type"), is(nullValue()));
    assertThat(
        yamlMapper.treeToValue(parsed.get("verified").get("by"), String.class),
        is("machine-confirmed:" + actor.getActor()));
    assertThat(parsed.get("verified").get("trust_tier"), is(nullValue()));
    assertThat(parsed.get("verified").get("actor"), is(nullValue()));
    assertThat(parsed.get("generated").get("actor"), is(nullValue()));
    assertThat(parsed.get("showMiniToc").asBoolean(), is(true));
    assertThat(parsed.get("allowTitleToDifferFromFilename").asBoolean(), is(true));
  }

  @Test
  public void omitsSchemaCrawlerSectionWhenNotProvided() throws Exception {
    final OkfFrontMatterYamlUtility utility = new OkfFrontMatterYamlUtility();
    final SchemaCrawlerActor actor = new SchemaCrawlerActor();
    final OkfFrontMatterRecord okfFrontMatter =
        new OkfFrontMatterRecord(
            "report",
            "Report",
            "Report",
            null,
            List.of(),
            new GeneratedRecord(actor, Instant.parse("2026-01-01T00:00:00Z")),
            new VerifiedRecord(TrustTier.machine_confirmed, actor),
            LifecycleStatus.stable);
    final GitHubPagesFrontMatterRecord gitHubPagesFrontMatter =
        new GitHubPagesFrontMatterRecord("Report", "Report", false, true);

    final JsonNode parsed =
        yamlMapper.readTree(utility.toYamlString(okfFrontMatter, gitHubPagesFrontMatter, null));

    assertThat(parsed.get("schema"), is(nullValue()));
    assertThat(parsed.get("name"), is(nullValue()));
    assertThat(parsed.get("counts"), is(nullValue()));
    assertThat(parsed.get("entity_type"), is(nullValue()));
  }

  @Test
  public void validatesRequiredFields() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new OkfFrontMatterRecord(
                " ",
                "PUBLIC.BOOKS",
                "Books table",
                null,
                List.of("table"),
                null,
                null,
                LifecycleStatus.stable));
    assertThrows(IllegalArgumentException.class, () -> LifecycleStatus.fromString("invalid"));
  }
}
