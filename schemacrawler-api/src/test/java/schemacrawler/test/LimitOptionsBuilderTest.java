package schemacrawler.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static schemacrawler.schema.RoutineType.function;
import static schemacrawler.schema.RoutineType.procedure;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.LimitOptionsBuilder;

public class LimitOptionsBuilderTest
{

  @Test
  public void routineTypes() {

    final LimitOptionsBuilder limitOptionsBuilder = LimitOptionsBuilder.builder();

    assertThat(limitOptionsBuilder.toOptions().getRoutineTypes(), contains(
      function, procedure));

    limitOptionsBuilder.routineTypes(new ArrayList<>());
    assertThat(limitOptionsBuilder.toOptions().getRoutineTypes(), is(empty()));

    limitOptionsBuilder.routineTypes(Arrays.asList(function));
    assertThat(limitOptionsBuilder.toOptions().getRoutineTypes(), contains(
      function));
  }

}
