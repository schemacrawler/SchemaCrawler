#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.executable.CommandRegistry;

public class TestCommandPlugin
{

  @Test
  public void testCommandPlugin()
    throws Exception
  {
    final CommandRegistry registry = new CommandRegistry();
    assertThat(registry.isCommandSupported("additional"), is(true));
  }

}
