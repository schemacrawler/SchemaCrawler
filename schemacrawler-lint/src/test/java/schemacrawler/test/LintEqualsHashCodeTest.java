package schemacrawler.test;


import org.junit.Ignore;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import schemacrawler.tools.lint.Lint;

public class LintEqualsHashCodeTest
{

  @Ignore
  @Test
  public void equalsContract()
  {
    EqualsVerifier.forClass(Lint.class).withIgnoredFields("value").verify();
  }

}
