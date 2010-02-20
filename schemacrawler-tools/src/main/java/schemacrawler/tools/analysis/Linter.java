package schemacrawler.tools.analysis;


import schemacrawler.schema.DatabaseObject;

public interface Linter<D extends DatabaseObject>
{

  void lint(D databaseObject);

}
