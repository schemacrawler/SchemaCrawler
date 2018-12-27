#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.executable.CommandRegistry;

public class TestCommandPlugin
{

  @Test
  public void testCommandPlugin()
    throws Exception
  {
    final CommandRegistry registry = new CommandRegistry();
    assertTrue(registry.isCommandSupported("additional"));
  }

}
