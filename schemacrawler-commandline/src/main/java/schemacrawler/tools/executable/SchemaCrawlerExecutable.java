package schemacrawler.tools.executable;


import schemacrawler.schema.Database;
import schemacrawler.tools.options.OutputOptions;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SchemaCrawlerExecutable
  extends BaseExecutable {

  private static final Logger LOGGER = Logger
    .getLogger(SchemaCrawlerExecutable.class.getName());

  public SchemaCrawlerExecutable(final String commands)
    throws Exception {
    super(commands);
  }

  @Override
  protected void executeOn(final Database database, final Connection connection)
    throws Exception {
    final CommandRegistry commandRegistry = new CommandRegistry();
    final Commands commands = new Commands(getCommand());
    final List<Executable> executables = new ArrayList<Executable>();
    for (final String command : commands) {
      final String commandExecutableClassName = commandRegistry
        .lookupCommandExecutableClassName(command);
      final Class<? extends Executable> commandExecutableClass = (Class<? extends Executable>) Class
        .forName(commandExecutableClassName);

      Executable executable;
      try {
        executable = commandExecutableClass.newInstance();
      }
      catch (final Exception e) {
        LOGGER.log(Level.FINE, "Could not instantiate "
          + commandExecutableClassName
          + " using the default constructor", e);
        final Constructor constructor = commandExecutableClass
          .getConstructor(new Class[]{
            String.class
          });
        executable = (Executable) constructor.newInstance(command);
      }

      executables.add(executable);
    }

    for (final Executable executable : executables) {
      executable.setSchemaCrawlerOptions(schemaCrawlerOptions);
      executable.setAdditionalConfiguration(additionalConfiguration);

      final String command = executable.getCommand();
      final OutputOptions executableOutputOptions = outputOptions.duplicate();
      if (commands.size() > 1) {
        if (commands.isFirstCommand(command)) {
          // First command - no footer
          executableOutputOptions.setNoFooter(true);
        }
        else if (commands.isLastCommand(command)) {
          // Last command - no header, or info
          executableOutputOptions.setNoHeader(true);
          executableOutputOptions.setNoInfo(true);

          executableOutputOptions.setAppendOutput(true);
        }
        else {
          // Middle command - no header, footer, or info
          executableOutputOptions.setNoHeader(true);
          executableOutputOptions.setNoInfo(true);
          executableOutputOptions.setNoFooter(true);

          executableOutputOptions.setAppendOutput(true);
        }
      }
      executable.setOutputOptions(executableOutputOptions);

      ((BaseExecutable) executable).executeOn(database, connection);
    }
  }
}
