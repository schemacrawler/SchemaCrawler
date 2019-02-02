#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package schemacrawler.integration.test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import schemacrawler.tools.databaseconnector.DatabaseConnectorRegistry;

public class BundledDistributionTest
{

  @Test
  public void testPlugin_newdb()
    throws Exception
  {
    final DatabaseConnectorRegistry registry = new DatabaseConnectorRegistry();
    assertThat(registry.hasDatabaseSystemIdentifier("newdb"), is(true));
  }

}
