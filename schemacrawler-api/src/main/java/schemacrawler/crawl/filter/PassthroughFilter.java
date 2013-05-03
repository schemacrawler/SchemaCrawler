package schemacrawler.crawl.filter;


import schemacrawler.schema.DatabaseObject;

class PassthroughFilter<D extends DatabaseObject>
  implements DatabaseObjectFilter<D>
{

  @Override
  public boolean include(D databaseObject)
  {
    return true;
  }

}
