package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.test.utility.ResolveTestContext;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestWriter;
import schemacrawler.tools.command.text.schema.SchemaTextCommandProvider;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;

@ResolveTestContext
public class PluginCommandTest {

  @Test
  public void testSchemaTextCommandProviderPluginCommand(final TestContext testContext) {
    final PluginCommand pluginCommand = new SchemaTextCommandProvider().getCommandLineCommand();

    assertThat(pluginCommand.getName(), is("command:schema"));

    final TestWriter testout = new TestWriter();
    try (final TestWriter out = testout) {
      out.println(pluginCommand.getHelpHeader());
      final String[] helpDescription = pluginCommand.getHelpDescription().get();
      writeStringArray(out, helpDescription);
      out.println();
      final Collection<PluginCommandOption> options = pluginCommand.getOptions();
      for (final PluginCommandOption pluginCommandOption : options) {
        out.println(pluginCommandOption.getName());
        final String[] optionHelpText = pluginCommandOption.getHelpText();
        writeStringArray(out, optionHelpText);
        out.println();
      }
      final String[] helpFooter = pluginCommand.getHelpFooter().get();
      writeStringArray(out, helpFooter);
    }
    assertThat(
        outputOf(testout), hasSameContentAs(classpathResource(testContext.testMethodFullName())));
  }

  private void writeStringArray(final TestWriter out, final String[] helpFooter) {
    for (final String helpLine : helpFooter) {
      out.println(helpLine);
    }
  }
}
