package schemacrawler.test;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresent;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
        .grepOnlyMatching(true)
        .includeGreppedColumns(new IncludeAll())
        .includeGreppedDefinitions(new IncludeAll())
        .includeGreppedRoutineParameters(new IncludeAll())
        .invertGrepMatch(true);
    final GrepOptionsBuilder builder =
        GrepOptionsBuilder.builder().fromOptions(builder1.toOptions());

    // After
    final GrepOptions grepOptions = builder.toOptions();
    assertThat(grepOptions.isGrepOnlyMatching(), is(true));
    assertThat(grepOptions.getGrepColumnInclusionRule(), isPresent());
    assertThat(grepOptions.getGrepDefinitionInclusionRule(), isPresent());
    assertThat(grepOptions.getGrepRoutineParameterInclusionRule(), isPresent());
    assertThat(grepOptions.isGrepOnlyMatching(), is(true));

    final GrepOptionsBuilder builderNull = GrepOptionsBuilder.builder().fromOptions(null);
    checkAllDefaults(builderNull.toOptions());
  }

  @Test
  public void grepOnlyMatching() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.grepOnlyMatching(true);
    // Check after setting the value
    assertThat(builder.toOptions().isGrepOnlyMatching(), is(true));
  }

  @Test
  public void includeGreppedColumns() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedColumns(new ExcludeAll());
    // Check after setting the value
    assertThat(builder.toOptions().getGrepColumnInclusionRule(), isPresentAndIs(new ExcludeAll()));
  }

  @Test
  public void includeGreppedColumnsPattern() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedColumns(Pattern.compile(".*"));
    // Check after setting the value
    assertThat(builder.toOptions().getGrepColumnInclusionRule(), isPresent());

    builder.includeGreppedColumns((Pattern) null);
    // Check after setting the value
    assertThat(builder.toOptions().getGrepColumnInclusionRule(), isEmpty());
    checkAllDefaults(builder.toOptions());
  }

  @Test
  public void includeGreppedDefinitions() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedDefinitions(new ExcludeAll());
    // Check after setting the value
    assertThat(
        builder.toOptions().getGrepDefinitionInclusionRule(), isPresentAndIs(new ExcludeAll()));
  }

  @Test
  public void includeGreppedDefinitionsPattern() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedDefinitions(Pattern.compile(".*"));
    // Check after setting the value
    assertThat(builder.toOptions().getGrepDefinitionInclusionRule(), isPresent());

    builder.includeGreppedDefinitions((Pattern) null);
    // Check after setting the value
    assertThat(builder.toOptions().getGrepDefinitionInclusionRule(), isEmpty());
    checkAllDefaults(builder.toOptions());
  }

  @Test
  public void includeGreppedRoutineParameters() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedRoutineParameters(new ExcludeAll());
    // Check after setting the value
    assertThat(
        builder.toOptions().getGrepRoutineParameterInclusionRule(),
        isPresentAndIs(new ExcludeAll()));
  }

  @Test
  public void includeGreppedRoutineParametersPattern() {
    final GrepOptionsBuilder builder = GrepOptionsBuilder.builder();
    checkAllDefaults(builder.toOptions());

    builder.includeGreppedRoutineParameters(Pattern.compile(".*"));
    // Check after setting the value
    assertThat(builder.toOptions().getGrepRoutineParameterInclusionRule(), isPresent());

    builder.includeGreppedRoutineParameters((Pattern) null);
    // Check after setting the value
    assertThat(builder.toOptions().getGrepRoutineParameterInclusionRule(), isEmpty());
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
    assertThat(grepOptionsNew.isGrepOnlyMatching(), is(false));
    assertThat(grepOptionsNew.getGrepColumnInclusionRule(), isEmpty());
    assertThat(grepOptionsNew.getGrepDefinitionInclusionRule(), isEmpty());
    assertThat(grepOptionsNew.getGrepRoutineParameterInclusionRule(), isEmpty());
    assertThat(grepOptionsNew.isGrepOnlyMatching(), is(false));
  }
}
