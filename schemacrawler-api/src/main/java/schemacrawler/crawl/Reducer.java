package schemacrawler.crawl;


import java.util.Collection;

import schemacrawler.schema.NamedObject;

// @FunctionalInterface
interface Reducer<N extends NamedObject>
{

  void reduce(final Collection<? extends N> namedObjects);

}
