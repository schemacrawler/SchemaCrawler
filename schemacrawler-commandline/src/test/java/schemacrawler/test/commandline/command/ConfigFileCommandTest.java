package schemacrawler.test.commandline.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static schemacrawler.test.utility.FileHasContent.classpathResource;
import static schemacrawler.test.utility.FileHasContent.hasNoContent;
import static schemacrawler.test.utility.FileHasContent.hasSameContentAs;
import static schemacrawler.test.utility.FileHasContent.outputOf;
import static schemacrawler.test.utility.TestUtility.savePropertiesToTempFile;
import static schemacrawler.tools.commandline.utility.CommandLineUtility.newCommandLine;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

import picocli.CommandLine;
import schemacrawler.test.utility.TestContext;
import schemacrawler.test.utility.TestContextParameterResolver;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.commandline.command.ConfigFileCommand;
import schemacrawler.tools.commandline.state.ShellState;
import schemacrawler.tools.options.Config;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(TestContextParameterResolver.class)
public class ConfigFileCommandTest {

  private static final String CONFIG_FILE_COMMAND_OUTPUT = "config_file_command_output";

  private TestOutputStream err;
  private TestOutputStream out;

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

  @AfterEach
  public void cleanUpStreams() {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void commandNoValue(final TestContext testContext) {
    final String[] args = {"-g"};

    final ShellState state = new ShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state), null);
    commandLine.execute(args);
    final Config config = state.getConfig();

    assertThat("Config is not null", config.size(), is(0));
    assertThat(config.containsKey(key), is(false));

    assertThat(outputOf(out), hasNoContent());
    assertThat(
        outputOf(err),
        hasSameContentAs(
            classpathResource(
                CONFIG_FILE_COMMAND_OUTPUT + testContext.testMethodName() + ".stderr.txt")));
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

  @BeforeEach
  public void setUpStreams() throws Exception {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }

  @Test
  public void withConfigFile() {
    final String[] args = {"-g", propertiesTempFile.toString(), "additional", "--extra"};

    final ShellState state = new ShellState();
    final CommandLine commandLine = newCommandLine(new ConfigFileCommand(state), null);
    commandLine.execute(args);
    final Config config = state.getConfig();

    assertThat("Config is not empty", config.size(), is(greaterThan(0)));
    assertThat("Test key not found in config", config.containsKey(key), is(true));
    assertThat("Test value not found in config", config.getStringValue(key, ""), is("value"));
  }
}
