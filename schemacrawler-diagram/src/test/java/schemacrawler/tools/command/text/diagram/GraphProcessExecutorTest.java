package schemacrawler.tools.command.text.diagram;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.test.utility.OnlyRunWithGraphviz;
import schemacrawler.test.utility.TestOutputStream;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;
import us.fatehi.utility.IOUtility;

public class GraphProcessExecutorTest {

  private final class TestGraphProcessExecutor extends AbstractGraphProcessExecutor {
    private TestGraphProcessExecutor(
        final Path dotFile, final Path outputFile, final DiagramOutputFormat diagramOutputFormat) {
      super(dotFile, outputFile, diagramOutputFormat);
    }

    @Override
    public boolean canGenerate() {
      return false;
    }

    @Override
    public void run() {}
  }

  private TestOutputStream err;
  private TestOutputStream out;

  @AfterEach
  public void cleanUpStreams() {
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
  }

  @Test
  public void constructorGraphProcessExecutor() throws IOException {

    final Path dotFile = Files.createTempFile("", "");
    Files.write(dotFile, "hello, world".getBytes());

    final Path outputFile = IOUtility.createTempFilePath("", "");

    final DiagramOutputFormat diagramOutputFormat = DiagramOutputFormat.png;

    // Assert no errors
    assertThat(
        new TestGraphProcessExecutor(dotFile, outputFile, diagramOutputFormat),
        is(not(nullValue())));

    // DOT file not readable
    final IORuntimeException exception1 =
        assertThrows(
            IORuntimeException.class,
            () ->
                new TestGraphProcessExecutor(
                    IOUtility.createTempFilePath("sc", "data"), outputFile, diagramOutputFormat));
    assertThat(exception1.getMessage(), containsString("Cannot read DOT file"));

    // Output file not writable
    final IORuntimeException exception2 =
        assertThrows(
            IORuntimeException.class,
            () ->
                new TestGraphProcessExecutor(
                    dotFile,
                    Paths.get("/not_a_directory/unwritable_file.dat"),
                    diagramOutputFormat));
    assertThat(exception2.getMessage(), containsString("Cannot write output file"));
  }

  @Test
  @OnlyRunWithGraphviz
  public void graphvizProcessExecutorError() throws IOException {

    final Path dotFile = Files.createTempFile("", "");
    Files.write(dotFile, "hello, world".getBytes());

    final Path outputFile = IOUtility.createTempFilePath("", "");

    final DiagramOutputFormat diagramOutputFormat = DiagramOutputFormat.png;

    // Graphviz error due to bad input
    final GraphvizProcessExecutor processExecutor =
        new GraphvizProcessExecutor(
            dotFile, outputFile, diagramOutputFormat, Collections.emptyList());
    processExecutor.run();
    assertThat(err.getContents(), containsString("syntax error in line 1 near 'hello'"));
  }

  @BeforeEach
  public void setUpStreams() throws Exception {
    out = new TestOutputStream();
    System.setOut(new PrintStream(out));

    err = new TestOutputStream();
    System.setErr(new PrintStream(err));
  }
}
