package schemacrawler.test;


import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.tools.lint.Lint;

public class LintEqualsHashCodeTest
{

  @Test
  public void equalsContract()
  {
    EqualsVerifier.forClass(Lint.class).verify();
  }

}
