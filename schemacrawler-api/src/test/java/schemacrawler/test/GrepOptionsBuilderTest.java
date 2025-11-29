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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import schemacrawler.inclusionrule.ExcludeAll;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.schemacrawler.GrepOptions;
import schemacrawler.schemacrawler.GrepOptionsBuilder;

public class GrepOptionsBuilderTest {

  @Test
  public void fromOptions() {
    final GrepOptionsBuilder builder1 = GrepOptionsBuilder.builder();
    builder1
        .includeGreppedColumns(new IncludeAll())
        .includeGreppedDefinitions(new IncludeAll())
        .includeGreppedRoutineParameters(new IncludeAll())
        .invertGrepMatch(true);
    final GrepOptionsBuilder builder =
        GrepOptionsBuilder.builder().fromOptions(builder1.toOptions());

    // After
    final GrepOptions grepOptions = builder.toOptions();
    assertThat(grepOptions.grepColumnInclusionRule(), is(not(nullValue())));
    assertThat(grepOptions.grepDefinitionInclusionRule(), is(not(nullValue())));
    assertThat(grepOptions.grepRoutineParameterInclusionRule(), is(not(nullValue())));

    final GrepOptionsBuilder builderNull = GrepOptionsBuilder.builder().fromOptions(null);
    checkAllDefaults(builderNull.toOptions());
  }

  @Test
  public void includeGreppedColumns() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedColumns(new ExcludeAll());
    // Check after setting the value
    assertThat(builder.toOptions().grepColumnInclusionRule(), is(new ExcludeAll()));
    assertThat(builder.toOptions().isGrepColumns(), is(true));
  }

  @Test
  public void includeGreppedColumnsPattern() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedColumns(Pattern.compile(".*"));
    // Check after setting the value
    assertThat(builder.toOptions().grepColumnInclusionRule(), is(not(nullValue())));

    builder.includeGreppedColumns((Pattern) null);
    // Check after setting the value
    assertThat(builder.toOptions().grepColumnInclusionRule(), is(nullValue()));
    checkAllDefaults(builder.toOptions());
  }

  @Test
  public void includeGreppedDefinitions() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedDefinitions(new ExcludeAll());
    // Check after setting the value
    assertThat(builder.toOptions().grepDefinitionInclusionRule(), is(new ExcludeAll()));
    assertThat(builder.toOptions().isGrepDefinitions(), is(true));
  }

  @Test
  public void includeGreppedDefinitionsPattern() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedDefinitions(Pattern.compile(".*"));
    // Check after setting the value
    assertThat(builder.toOptions().grepDefinitionInclusionRule(), is(not(nullValue())));

    builder.includeGreppedDefinitions((Pattern) null);
    // Check after setting the value
    assertThat(builder.toOptions().grepDefinitionInclusionRule(), is(nullValue()));
    checkAllDefaults(builder.toOptions());
  }

  @Test
  public void includeGreppedRoutineParameters() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedRoutineParameters(new ExcludeAll());
    // Check after setting the value
    assertThat(builder.toOptions().grepRoutineParameterInclusionRule(), is(new ExcludeAll()));
    assertThat(builder.toOptions().isGrepRoutineParameters(), is(true));
  }

  @Test
  public void includeGreppedRoutineParametersPattern() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedRoutineParameters(Pattern.compile(".*"));
    // Check after setting the value
    assertThat(builder.toOptions().grepRoutineParameterInclusionRule(), is(not(nullValue())));

    builder.includeGreppedRoutineParameters((Pattern) null);
    // Check after setting the value
    assertThat(builder.toOptions().grepRoutineParameterInclusionRule(), is(nullValue()));
    checkAllDefaults(builder.toOptions());
  }

  @Test
  public void isGrepInvertMatch() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.invertGrepMatch(true);
    // Check after setting the value
    assertThat(builder.toOptions().isGrepInvertMatch(), is(true));
  }

  @Test
  public void newOptions() {
    final GrepOptions grepOptions = GrepOptionsBuilder.newGrepOptions();
    checkAllDefaults(grepOptions);
  }

  private void checkAllDefaults(final GrepOptions grepOptionsNew) {
    assertThat(grepOptionsNew.grepColumnInclusionRule(), is(nullValue()));
    assertThat(grepOptionsNew.grepDefinitionInclusionRule(), is(nullValue()));
    assertThat(grepOptionsNew.grepRoutineParameterInclusionRule(), is(nullValue()));

    // Additional tests for synthetic methods
    assertThat(grepOptionsNew.isGrepColumns(), is(false));
    assertThat(grepOptionsNew.isGrepDefinitions(), is(false));
    assertThat(grepOptionsNew.isGrepRoutineParameters(), is(false));
    assertThat(grepOptionsNew.isGrepInvertMatch(), is(false));
  }
}
