package schemacrawler.test;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.condition.JRE.JAVA_8;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import javax.script.ScriptEngineFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.JRE;
import schemacrawler.tools.commandline.command.AvailableCatalogLoaders;
import schemacrawler.tools.commandline.command.AvailableCommands;
import schemacrawler.tools.commandline.command.AvailableJDBCDrivers;
import schemacrawler.tools.commandline.command.AvailableScriptEngines;
import schemacrawler.tools.commandline.command.AvailableServers;

public class AvailablePluginsTest {

  @Test
  public void availableCatalogLoaders() {
    assertThat(
        new AvailableCatalogLoaders(),
        contains(
            "weakassociationsloader",
            "testloader",
            "attributesloader",
            "countsloader",
            "schemacrawlerloader"));
  }

  @Test
  public void availableCommands() {
    assertThat(
        new AvailableCommands(),
        contains(
            "brief", "count", "details", "dump", "list", "quickdump", "schema", "test-command"));
  }

  @Test
  public void availableScriptEngines() throws UnsupportedEncodingException {
    final AvailableScriptEngines availableScriptEngines = new AvailableScriptEngines();
    final int size = availableScriptEngines.size();
    assertThat("Incorrect number of script engines found", size == 1 || size == 0, is(true));
    if (JRE.currentVersion() != JAVA_8 && size == 0) {
      // No script engines ship with Java versions later than 8
      return;
    }

    final List<ScriptEngineFactory> availableScriptEnginesList = new ArrayList<>();
    availableScriptEngines.forEach(availableScriptEnginesList::add);

    assertThat(
        availableScriptEnginesList.stream()
            .map(driver -> driver.getClass().getTypeName())
            .collect(toList()),
        hasItem("jdk.nashorn.api.scripting.NashornScriptEngineFactory"));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final String utf8 = StandardCharsets.UTF_8.name();
    try (final PrintStream out = new PrintStream(baos, true, utf8)) {
      availableScriptEngines.print(out);
    }
    final String data = baos.toString(utf8);

    assertThat(data.replace("\r", ""), containsString("Available script engines:"));
    assertThat(data.replace("\r", ""), containsString("Nashorn"));
  }

  @Test
  public void availableJDBCDrivers() throws UnsupportedEncodingException {
    final AvailableJDBCDrivers availableJDBCDrivers = new AvailableJDBCDrivers();
    final int size = availableJDBCDrivers.size();
    assertThat(size == 1 || size == 2, is(true));

    final List<Driver> availableJDBCDriversList = new ArrayList<>();
    availableJDBCDrivers.forEach(availableJDBCDriversList::add);

    assertThat(
        availableJDBCDriversList.stream()
            .map(driver -> driver.getClass().getTypeName())
            .collect(toList()),
        hasItem("org.hsqldb.jdbc.JDBCDriver"));

    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final String utf8 = StandardCharsets.UTF_8.name();
    try (final PrintStream out = new PrintStream(baos, true, utf8)) {
      availableJDBCDrivers.print(out);
    }
    final String data = baos.toString(utf8);

    assertThat(data.replace("\r", ""), containsString("Available JDBC drivers:"));
    assertThat(data.replace("\r", ""), containsString("org.hsqldb.jdbc.JDBCDriver"));
  }

  @Test
  public void availableServers() {
    assertThat(new AvailableServers(), contains("test-db"));
  }
}
