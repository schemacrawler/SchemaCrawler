package schemacrawler.schema;


import java.util.Collection;

// @FunctionalInterface
public interface Reducer<N extends NamedObject>
{

  void reduce(final Collection<? extends N> namedObjects);

}
