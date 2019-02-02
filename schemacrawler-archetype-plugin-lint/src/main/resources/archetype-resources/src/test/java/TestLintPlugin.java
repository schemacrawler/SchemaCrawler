#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.lint.LinterRegistry;

public class TestLintPlugin
{

  @Test
  public void testLintPlugin()
    throws Exception
  {
    final LinterRegistry registry = new LinterRegistry();
    assertThat(registry.hasLinter("${package}.AdditionalLinter"), is(true));
  }

}
