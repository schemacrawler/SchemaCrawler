package schemacrawler.test;


import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.tools.lint.SimpleLint;

public class LintEqualsHashCodeTest
{

  @Test
  public void equalsContract()
  {
    EqualsVerifier.forClass(SimpleLint.class).verify();
  }

}
