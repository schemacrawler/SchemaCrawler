package schemacrawler.tools.lint;


public class LinterHelp
{

  public static String getLinterHelpText()
    throws Exception
  {
    final StringBuilder buffer = new StringBuilder(1024);

    buffer.append("--- Available Linters ---").append(System.lineSeparator())
      .append(System.lineSeparator());

    final LinterRegistry registry = new LinterRegistry();
    for (final String linterId: registry)
    {
      final Linter linter = registry.newLinter(linterId);

      buffer.append("Linter: ").append(linter.getId())
        .append(System.lineSeparator());
      buffer.append(linter.getDescription()).append(System.lineSeparator());
    }

    return buffer.toString();
  }

  public static void main(final String[] args)
    throws Exception
  {
    System.out.println(getLinterHelpText());
  }

  private LinterHelp()
  {
    // Prevent instantiation
  }

}
