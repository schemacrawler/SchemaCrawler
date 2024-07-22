package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.condition.JRE.JAVA_8;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.JRE;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.registry.ScriptEngineRegistry;
import us.fatehi.utility.property.PropertyName;

public class ScriptEngineRegistryTest {

  @Test
  public void commandLineCommands() {
    final ScriptEngineRegistry driverRegistry = ScriptEngineRegistry.getScriptEngineRegistry();
    final Collection<PluginCommand> commandLineCommands = driverRegistry.getCommandLineCommands();
    assertThat(commandLineCommands, is(empty()));
  }

  @Test
  public void helpCommands() {
    final ScriptEngineRegistry driverRegistry = ScriptEngineRegistry.getScriptEngineRegistry();
    final Collection<PluginCommand> commandLineCommands = driverRegistry.getHelpCommands();
    assertThat(commandLineCommands, is(empty()));
  }

  @Test
  public void commandDescriptions() {
    if (JRE.currentVersion() != JAVA_8) {
      // No script engines ship with Java versions later than 8
      return;
    }
    final ScriptEngineRegistry driverRegistry = ScriptEngineRegistry.getScriptEngineRegistry();
    final Collection<PropertyName> commandLineCommands = driverRegistry.getCommandDescriptions();
    assertThat(commandLineCommands, hasSize(1));
  }

  @Test
  public void name() {
    final ScriptEngineRegistry driverRegistry = ScriptEngineRegistry.getScriptEngineRegistry();
    assertThat(driverRegistry.getName(), is("script engines"));
  }
}
