package schemacrawler.test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.registry.JDBCDriverRegistry;
import us.fatehi.test.utility.TestDatabaseDriver;
import us.fatehi.utility.property.PropertyName;

public class JDBCDriverRegistryTest {

  @Test
  public void commandLineCommands() {
    final JDBCDriverRegistry driverRegistry = JDBCDriverRegistry.getJDBCDriverRegistry();
    final Collection<PluginCommand> commandLineCommands = driverRegistry.getCommandLineCommands();
    assertThat(commandLineCommands, is(empty()));
  }

  @Test
  public void helpCommands() {
    final JDBCDriverRegistry driverRegistry = JDBCDriverRegistry.getJDBCDriverRegistry();
    final Collection<PluginCommand> commandLineCommands = driverRegistry.getHelpCommands();
    assertThat(commandLineCommands, is(empty()));
  }

  @Test
  public void commandDescriptions() {
    final JDBCDriverRegistry driverRegistry = JDBCDriverRegistry.getJDBCDriverRegistry();
    final Collection<PropertyName> commandLineCommands =
        driverRegistry.getCommandDescriptions();
    assertThat(commandLineCommands, hasSize(2));
  }

  @Test
  public void name() {
    final JDBCDriverRegistry driverRegistry = JDBCDriverRegistry.getJDBCDriverRegistry();
    assertThat(driverRegistry.getName(), is("JDBC drivers"));
  }

  @Test
  public void loadError() throws Exception {
    restoreSystemProperties(
        () -> {
          System.setProperty(
              TestDatabaseDriver.class.getName() + ".force-instantiation-failure", "throw");

          assertThrows(InternalRuntimeException.class, () -> JDBCDriverRegistry.reload());
        });
    // Reset
    JDBCDriverRegistry.reload();
  }
}
