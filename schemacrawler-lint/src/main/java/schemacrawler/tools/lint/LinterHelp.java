package schemacrawler.tools.lint;


public class LinterHelp
{

  public static void main(final String[] args)
    throws Exception
  {
    final LinterRegistry registry = new LinterRegistry();
    for (final String linterId: registry)
    {
      final Linter linter = registry.newLinter(linterId);
      System.out.println(linter.getId());
      System.out.println(linter.getDescription());
      System.out.println();
    }
  }

  private LinterHelp()
  {
    // Prevent instantiation
  }

}
