package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Predicate;

import schemacrawler.filter.DatabaseObjectFilter;
import schemacrawler.filter.IncludeAllFilter;
import schemacrawler.filter.InclusionRuleFilter;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Reducer;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

public final class ReducerFactory
{

  private static abstract class BaseReducer<N extends NamedObject>
    implements Reducer<N>
  {

    private final Predicate<N> filter;

    protected BaseReducer(final Predicate<N> filter)
    {
      this.filter = requireNonNull(filter);
    }

    @Override
    public void reduce(final Collection<? extends N> allNamedObjects)
    {
      if (allNamedObjects != null)
      {
        final Collection<N> keepList = new HashSet<>();
        for (final N namedObject: allNamedObjects)
        {
          if (filter.test(namedObject))
          {
            keepList.add(namedObject);
          }
        }
        allNamedObjects.retainAll(keepList);
      }
    }

  }

  public static Reducer<Routine> getRoutineReducer(final Predicate<Routine> routineFilter)
  {
    return new BaseReducer<Routine>(routineFilter)
    {
    };
  }

  public static Reducer<Schema> getSchemaReducer(final SchemaCrawlerOptions options)
  {
    final Predicate<Schema> schemaFilter;
    if (options == null)
    {
      schemaFilter = new IncludeAllFilter<Schema>();
    }
    else
    {
      schemaFilter = new InclusionRuleFilter<Schema>(options
        .getSchemaInclusionRule(), true);
    }

    return new BaseReducer<Schema>(schemaFilter)
    {
    };
  }

  public static Reducer<Sequence> getSequenceReducer(final SchemaCrawlerOptions options)
  {
    final Predicate<Sequence> sequenceFilter;
    if (options == null)
    {
      sequenceFilter = new IncludeAllFilter<Sequence>();
    }
    else
    {
      sequenceFilter = new DatabaseObjectFilter<Sequence>(options,
                                                          options
                                                            .getSequenceInclusionRule());
    }
    return new BaseReducer<Sequence>(sequenceFilter)
    {
    };
  }

  public static Reducer<Synonym> getSynonymReducer(final SchemaCrawlerOptions options)
  {
    final Predicate<Synonym> synonymFilter;
    if (options == null)
    {
      synonymFilter = new IncludeAllFilter<Synonym>();
    }
    else
    {
      synonymFilter = new DatabaseObjectFilter<Synonym>(options,
                                                        options
                                                          .getSynonymInclusionRule());
    }

    return new BaseReducer<Synonym>(synonymFilter)
    {
    };
  }

  private ReducerFactory()
  {
    // Prevent instantiation
  }

}
