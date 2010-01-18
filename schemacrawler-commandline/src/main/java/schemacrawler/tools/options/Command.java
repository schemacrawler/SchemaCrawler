package schemacrawler.tools.options;


import schemacrawler.schemacrawler.Options;

public final class Command
  implements Options {

  private static final long serialVersionUID = -3450943894546747834L;

  private final String command;

  public Command() {
    this(null);
  }

  public Command(final String command) {
    this.command = command;
  }

  @Override
  public String toString() {
    return command;
  }

}
