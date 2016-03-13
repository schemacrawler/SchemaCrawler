package schemacrawler.crawl;


import org.junit.Ignore;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.schema.SchemaReference;

public class SchemaEqualsHashCodeTest
{

  @Test
  public void equalsContract1()
  {
    EqualsVerifier.forClass(SchemaReference.class).verify();
  }

  @Ignore
  @Test
  public void equalsContract2()
  {
    EqualsVerifier.forClass(MutableCatalog.class).verify();
    EqualsVerifier.forClass(MutableTable.class).verify();
    EqualsVerifier.forClass(MutablePrimaryKey.class).verify();
    EqualsVerifier.forClass(MutableColumn.class).verify();
    EqualsVerifier.forClass(MutableForeignKey.class).verify();
    EqualsVerifier.forClass(MutableForeignKeyColumnReference.class).verify();
    EqualsVerifier.forClass(MutablePrivilege.class).verify();
  }

}
