package schemacrawler.schema;


import java.util.function.Predicate;

public interface ReducibleCollection<N extends NamedObject>
  extends Iterable<N>
{

  void filter(Predicate<? super N> predicate);

  boolean isFiltered(NamedObject object);

}
