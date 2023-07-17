package schemacrawler.filter;

import static java.util.Objects.requireNonNull;
import static schemacrawler.filter.FilterFactory.routineFilter;
import static schemacrawler.filter.FilterFactory.schemaFilter;
import static schemacrawler.filter.FilterFactory.sequenceFilter;
import static schemacrawler.filter.FilterFactory.synonymFilter;
import static schemacrawler.filter.FilterFactory.tableFilter;
import java.util.function.Predicate;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Reducer;
import schemacrawler.schema.ReducibleCollection;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public final class ReducerFactory {

  private static class FilteringReducer<N extends NamedObject> implements Reducer<N> {

    private final Predicate<N> filter;

    protected FilteringReducer(final Predicate<N> filter) {
      this.filter = requireNonNull(filter, "No filter provided");
    }

    @Override
    public void reduce(final ReducibleCollection<? extends N> allNamedObjects) {
      requireNonNull(allNamedObjects, "No named objects provided");
      allNamedObjects.filter(filter);
    }

    @Override
    public void undo(final ReducibleCollection<? extends N> allNamedObjects) {
      requireNonNull(allNamedObjects, "No named objects provided");
      allNamedObjects.resetFilters();
    }
  }

  public static Reducer<Routine> getRoutineReducer(final SchemaCrawlerOptions options) {
    return new FilteringReducer<>(routineFilter(options));
  }

  public static Reducer<Schema> getSchemaReducer(final SchemaCrawlerOptions options) {
    return new FilteringReducer<>(schemaFilter(options));
  }

  public static Reducer<Sequence> getSequenceReducer(final SchemaCrawlerOptions options) {
    return new FilteringReducer<>(sequenceFilter(options));
  }

  public static Reducer<Synonym> getSynonymReducer(final SchemaCrawlerOptions options) {
    return new FilteringReducer<>(synonymFilter(options));
  }

  public static Reducer<Table> getTableReducer(final Predicate<Table> tableFilter) {
    return new FilteringReducer<>(tableFilter);
  }

  public static Reducer<Table> getTableReducer(final SchemaCrawlerOptions options) {
    return new TablesReducer(options, tableFilter(options));
  }

  private ReducerFactory() {
    // Prevent instantiation
  }
}
