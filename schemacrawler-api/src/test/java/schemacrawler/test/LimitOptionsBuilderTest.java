package schemacrawler.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static schemacrawler.schema.RoutineType.function;
import static schemacrawler.schema.RoutineType.procedure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import schemacrawler.schema.RoutineType;
import schemacrawler.schemacrawler.LimitOptions;
import schemacrawler.schemacrawler.LimitOptionsBuilder;

public class LimitOptionsBuilderTest {

  @Test
  public void routineTypes() {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    LimitOptions limitOptions;
    LimitOptions limitOptionsPlayback;

    // 1. Test defaults
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getRoutineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getRoutineTypes(), containsInAnyOrder(function, procedure));

    // 2. Test empty collection
    limitOptionsBuilder.routineTypes(new ArrayList<>());
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getRoutineTypes(), is(empty()));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getRoutineTypes(), is(empty()));

    // 3. Test collection with non-defaults
    limitOptionsBuilder.routineTypes(Arrays.asList(function));
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getRoutineTypes(), containsInAnyOrder(function));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getRoutineTypes(), containsInAnyOrder(function));

    // 4. Test null collection (which resets to defaults)
    limitOptionsBuilder.routineTypes((Collection<RoutineType>) null);
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getRoutineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getRoutineTypes(), containsInAnyOrder(function, procedure));
  }

  @Test
  public void routineTypesWithString() {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    LimitOptions limitOptions;
    LimitOptions limitOptionsPlayback;

    // 1. Test defaults
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getRoutineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getRoutineTypes(), containsInAnyOrder(function, procedure));

    // 2. Test empty string
    limitOptionsBuilder.routineTypes("");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getRoutineTypes(), is(empty()));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getRoutineTypes(), is(empty()));

    // 3. Test string with non-defaults
    limitOptionsBuilder.routineTypes("function");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getRoutineTypes(), containsInAnyOrder(function));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getRoutineTypes(), containsInAnyOrder(function));

    // 3. Test null string (which resets to defaults)
    limitOptionsBuilder.routineTypes((String) null);
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getRoutineTypes(), containsInAnyOrder(function, procedure));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getRoutineTypes(), containsInAnyOrder(function, procedure));
  }

  @Test
  public void tableTypes() {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();
    LimitOptions limitOptions;
    LimitOptions limitOptionsPlayback;

    // 1. Test defaults
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(
        limitOptions.getTableTypes().toArray(), is(new String[] {"TABLE", "VIEW", "BASE TABLE"}));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(
        limitOptionsPlayback.getTableTypes().toArray(),
        is(new String[] {"TABLE", "VIEW", "BASE TABLE"}));

    // 2. Test empty collection
    limitOptionsBuilder.tableTypes(new ArrayList<>());
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getTableTypes().toArray(), is(new String[0]));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getTableTypes().toArray(), is(new String[0]));

    // 3. Test collection with non-defaults
    limitOptionsBuilder.tableTypes("TABLE");
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getTableTypes().toArray(), is(new String[] {"TABLE"}));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getTableTypes().toArray(), is(new String[] {"TABLE"}));

    // 4. Test null collection (which resets to defaults)
    limitOptionsBuilder.tableTypes((Collection<String>) null);
    limitOptions = limitOptionsBuilder.toOptions();
    assertThat(limitOptions.getTableTypes().toArray(), is(nullValue()));
    limitOptionsPlayback = LimitOptionsBuilder.builder().fromOptions(limitOptions).toOptions();
    assertThat(limitOptionsPlayback.getTableTypes().toArray(), is(nullValue()));
  }
}
