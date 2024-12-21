package schemacrawler.tools.commandline;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import schemacrawler.tools.commandline.command.ConnectCommand;
import schemacrawler.tools.commandline.shell.SystemCommand;

@Command(name = "database-connection-test")
public class ConnectionTestCommands {
  @Mixin private ConnectCommand connect;
  @Mixin private SystemCommand system;
}
