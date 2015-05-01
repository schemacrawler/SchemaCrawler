package schemacrawler.test.utility;


import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class SiteVariationsGenerationRule
  implements TestRule
{

  private static final boolean GENERATE_SITE = true;

  @Override
  public Statement apply(final Statement base, final Description description)
  {
    return new Statement()
    {
      @Override
      public void evaluate()
        throws Throwable
      {
        if (GENERATE_SITE)
        {
          base.evaluate();
        }
      }
    };
  }

}
