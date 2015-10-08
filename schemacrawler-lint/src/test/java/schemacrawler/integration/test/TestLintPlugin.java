package schemacrawler.integration.test;


import static org.junit.Assert.assertTrue;

import org.junit.Test;

import schemacrawler.tools.lint.LinterRegistry;

public class TestLintPlugin
{

  @Test
  public void testLintPlugin()
    throws Exception
  {
    final LinterRegistry registry = new LinterRegistry();
    for (final String linter: new String[] {
                                             "schemacrawler.tools.linter.LinterColumnTypes",
                                             "schemacrawler.tools.linter.LinterForeignKeyMismatch",
                                             "schemacrawler.tools.linter.LinterForeignKeyWithNoIndexes",
                                             "schemacrawler.tools.linter.LinterNullColumnsInIndex",
                                             "schemacrawler.tools.linter.LinterNullIntendedColumns",
                                             "schemacrawler.tools.linter.LinterRedundantIndexes",
                                             "schemacrawler.tools.linter.LinterTableCycles",
                                             "schemacrawler.tools.linter.LinterTableWithIncrementingColumns",
                                             "schemacrawler.tools.linter.LinterTableWithNoIndexes",
                                             "schemacrawler.tools.linter.LinterTableWithQuotedNames",
                                             "schemacrawler.tools.linter.LinterTableWithSingleColumn",
                                             "schemacrawler.tools.linter.LinterTooManyLobs",
                                             "schemacrawler.tools.linter.LinterUselessSurrogateKey", })
    {
      assertTrue(registry.hasLinter(linter));
    }
  }

}
