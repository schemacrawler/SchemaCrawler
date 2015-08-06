package schemacrawler.test;


import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import schemacrawler.schema.JavaSqlType;
import sf.util.graph.DirectedEdge;
import sf.util.graph.Vertex;

public class SchemaEqualsHashCodeTest
{

  @Test
  public void equalsContract()
  {
    EqualsVerifier.forClass(JavaSqlType.class).verify();
    EqualsVerifier.forClass(Vertex.class).suppress(Warning.NULL_FIELDS)
      .verify();
    EqualsVerifier.forClass(DirectedEdge.class).suppress(Warning.NULL_FIELDS)
      .verify();
  }

}
