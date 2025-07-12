/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.test.utility.testcommand;

import java.io.PrintWriter;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import us.fatehi.utility.property.PropertyName;

public final class TestCommand extends BaseSchemaCrawlerCommand<TestOptions> {

  static final PropertyName COMMAND =
      new PropertyName("test-command", "Test command which is not deployed with the release");

  public TestCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() {
    // No-op
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {

    final boolean usesConnection = usesConnection();
    final boolean hasConnection = connection != null;
    if (usesConnection != hasConnection) {
      throw new RuntimeException("Uses connection not honored");
    }

    try (final PrintWriter writer = outputOptions.openNewOutputWriter()) {
      writer.println("Output generated from " + this.getClass().getName());
      writer.println(commandOptions);
      writer.flush();
    }
  }

  @Override
  public boolean usesConnection() {
    return getCommandOptions().usesConnection();
  }
}
