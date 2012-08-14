package schemacrawler.crawl;


import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Ignore;
import org.junit.Test;

import schemacrawler.schema.SchemaReference;

@Ignore
public class SchemaEqualsHashCodeTest
{

  @Test
  public void equalsContract()
  {
    EqualsVerifier.forClass(MutableDatabase.class).verify();
    EqualsVerifier.forClass(SchemaReference.class).verify();
    EqualsVerifier.forClass(MutableTable.class).verify();
    EqualsVerifier.forClass(MutablePrimaryKey.class).verify();
    EqualsVerifier.forClass(MutableColumn.class).verify();
    EqualsVerifier.forClass(MutableForeignKey.class).verify();
    EqualsVerifier.forClass(MutableForeignKeyColumnReference.class).verify();
    EqualsVerifier.forClass(MutablePrivilege.class).verify();
  }

}
