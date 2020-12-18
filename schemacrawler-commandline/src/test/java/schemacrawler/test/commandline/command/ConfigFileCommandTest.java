package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.TestUtility.savePropertiesToTempFile;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.typesafe.config.ConfigFactory;

import picocli.CommandLine;
import schemacrawler.tools.commandline.command.ConfigFileCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.options.Config;

@TestInstance(Lifecycle.PER_CLASS)
public class ConfigFileCommandTest {

  private Path propertiesTempFile;
  private String key;

  @Test
  public void allArgs() {
    final String[] args = {"-g", "a_file", "additional", "--extra"};

    final ShellState state = new ShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state), null);
    commandLine.execute(args);
    final Config config = state.getConfig();

    assertThat("Config is not empty", config.size(), is(greaterThan(0)));
    assertThat(config.containsKey(key), is(false));
  }

  @BeforeEach
  public void clearConfigCaches() {
    ConfigFactory.invalidateCaches();
  }

  @Test
  public void commandNoValue() {
    final String[] args = {"-g"};

    final ShellState state = new ShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state), null);
    commandLine.execute(args);
    final Config config = state.getConfig();

    assertThat("Config is not null", config.size(), is(0));
    assertThat(config.containsKey(key), is(false));
  }

  @BeforeAll
  public void createProperties() throws IOException {
    key = "aaaa_" + RandomStringUtils.randomAlphanumeric(8);
    final Properties properties = new Properties();
    properties.setProperty(key, "value");
    propertiesTempFile = savePropertiesToTempFile(properties);
  }

  @Test
  public void noArgs() {
    final String[] args = new String[0];

    final ShellState state = new ShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state), null);
    commandLine.execute(args);
    final Config config = state.getConfig();

    assertThat("Config is not empty", config.size(), is(greaterThan(0)));
    assertThat(config.containsKey(key), is(false));
  }

  @Test
  public void noValidArgs() {
    final String[] args = {"--some-option"};

    final ShellState state = new ShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state), null);
    commandLine.execute(args);
    final Config config = state.getConfig();

    assertThat("Config is not empty", config.size(), is(greaterThan(0)));
    assertThat(config.containsKey(key), is(false));
  }

  @Test
  public void withConfigFile() {
    final String[] args = {"-g", propertiesTempFile.toString(), "additional", "--extra"};

    final ShellState state = new ShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state), null);
    commandLine.execute(args);
    final Config config = state.getConfig();

    assertThat("Config is not empty", config.size(), is(greaterThan(0)));
    System.out.println(config);
    assertThat(config.containsKey(key), is(true));
    assertThat(config.getStringValue(key, ""), is("value"));
  }
}
