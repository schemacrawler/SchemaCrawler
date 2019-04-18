package schemacrawler.test.commandline.parser;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static us.fatehi.commandlineparser.CommandLineUtility.newCommandLine;

import java.io.PrintStream;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import picocli.CommandLine;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.schemacrawler.SchemaCrawlerOptionsBuilder;
import schemacrawler.tools.commandline.parser.FilterOptionsParser;
import schemacrawler.tools.commandline.state.SchemaCrawlerShellState;

public class FilterOptionsParserTest
{

  @Test
  public void noArgs()
  {
    final String[] args = new String[0];

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    final FilterOptionsParser optionsParser = new FilterOptionsParser(state);
    newCommandLine(new FilterOptionsParser(state))
      .parseWithHandlers(new picocli.CommandLine.RunLast(),
                         new picocli.CommandLine.DefaultExceptionHandler<>(),
                         args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getParentTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.getChildTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.isNoEmptyTables(), is(false));
  }

  @Test
  public void noValidArgs()
  {
    final String[] args = { "--some-option" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    final FilterOptionsParser optionsParser = new FilterOptionsParser(state);
    newCommandLine(new FilterOptionsParser(state))
      .parseWithHandlers(new picocli.CommandLine.RunLast(),
                         new ThrowExceptionHandler<>(),
                         args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getParentTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.getChildTableFilterDepth(), is(0));
    assertThat(schemaCrawlerOptions.isNoEmptyTables(), is(false));
  }

  @Test
  public void parentsBadValue()
  {
    final String[] args = { "--parents", "-1" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> newCommandLine(new FilterOptionsParser(state))
                   .parseWithHandlers(new picocli.CommandLine.RunLast(),
                                      new ThrowExceptionHandler<>(),
                                      args));
  }

  public static class ThrowExceptionHandler<R>
    extends
    CommandLine.AbstractHandler<R, ThrowExceptionHandler<R>>
    implements CommandLine.IExceptionHandler2<R>
  {
    public List<Object> handleException(CommandLine.ParameterException ex,
                                        PrintStream out,
                                        CommandLine.Help.Ansi ansi,
                                        String... args)
    {
      internalHandleParseException(ex, out, ansi, args);
      return Collections.<Object>emptyList();
    }

    /**
     * Prints the message of the specified exception, followed by the usage message for the command or subcommand
     * whose input was invalid, to the stream returned by {@link #err()}.
     *
     * @param ex   the ParameterException describing the problem that occurred while parsing the command line arguments,
     *             and the CommandLine representing the command or subcommand whose input was invalid
     * @param args the command line arguments that could not be parsed
     * @return the empty list
     * @since 3.0
     */
    public R handleParseException(CommandLine.ParameterException ex,
                                  String[] args)
    {
      internalHandleParseException(ex, err(), ansi(), args);
      return returnResultOrExit(null);
    }

    private void internalHandleParseException(CommandLine.ParameterException ex,
                                              PrintStream out,
                                              CommandLine.Help.Ansi ansi,
                                              String[] args)
    {
      throw ex;
    }

    /**
     * This implementation always simply rethrows the specified exception.
     *
     * @param ex          the ExecutionException describing the problem that occurred while executing the {@code Runnable} or {@code Callable} command
     * @param parseResult the result of parsing the command line arguments
     * @return nothing: this method always rethrows the specified exception
     * @throws CommandLine.ExecutionException always rethrows the specified exception
     * @since 3.0
     */
    public R handleExecutionException(CommandLine.ExecutionException ex,
                                      CommandLine.ParseResult parseResult)
    {
      return throwOrExit(ex);
    }

    @Override
    protected ThrowExceptionHandler<R> self()
    {
      return this;
    }
  }

  @Test
  public void childrenBadValue()
  {
    final String[] args = { "--children", "-1" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> newCommandLine(new FilterOptionsParser(state))
                   .parseWithHandlers(new picocli.CommandLine.RunLast(),
                                      new ThrowExceptionHandler<>(),
                                      args));
  }

  @Test
  public void parentsNoValue()
  {
    final String[] args = { "--parents" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> newCommandLine(new FilterOptionsParser(state))
                   .parseWithHandlers(new picocli.CommandLine.RunLast(),
                                      new ThrowExceptionHandler<>(),
                                      args));
  }

  @Test
  public void childrenNoValue()
  {
    final String[] args = { "--children" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    assertThrows(CommandLine.ParameterException.class,
                 () -> newCommandLine(new FilterOptionsParser(state))
                   .parseWithHandlers(new picocli.CommandLine.RunLast(),
                                      new ThrowExceptionHandler<>(),
                                      args));
  }

  @Test
  public void allArgs()
  {
    final String[] args = {
      "--parents",
      "2",
      "--children",
      "2",
      "--no-empty-tables=true",
      "additional",
      "-extra" };

    final SchemaCrawlerOptionsBuilder builder = SchemaCrawlerOptionsBuilder
      .builder();
    final SchemaCrawlerShellState state = new SchemaCrawlerShellState();
    state.setSchemaCrawlerOptionsBuilder(builder);
    final FilterOptionsParser optionsParser = new FilterOptionsParser(state);
    newCommandLine(new FilterOptionsParser(state))
      .parseWithHandlers(new picocli.CommandLine.RunLast(),
                         new picocli.CommandLine.DefaultExceptionHandler<>(),
                         args);
    final SchemaCrawlerOptions schemaCrawlerOptions = builder.toOptions();

    assertThat(schemaCrawlerOptions.getParentTableFilterDepth(), is(2));
    assertThat(schemaCrawlerOptions.getChildTableFilterDepth(), is(2));
    assertThat(schemaCrawlerOptions.isNoEmptyTables(), is(true));
  }

}
