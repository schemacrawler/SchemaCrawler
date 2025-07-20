/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static us.fatehi.utility.IOUtility.createTempFilePath;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import us.fatehi.utility.string.StringFormat;

public class ProcessExecutor implements Callable<Integer> {

  private static final Logger LOGGER = Logger.getLogger(ProcessExecutor.class.getName());

  private List<String> command;
  private Path processOutput;
  private Path processError;
  private int exitCode;

  @Override
  public Integer call() {

    try {
      requireNonNull(command, "No command provided");

      if (command.isEmpty()) {
        return null;
      }

      processOutput = createTempFilePath("temp", "stdout");
      processError = createTempFilePath("temp", "stderr");

      LOGGER.log(Level.CONFIG, new StringFormat("Executing:%n%s", command));

      final ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectOutput(processOutput.toFile());
      processBuilder.redirectError(processError.toFile());

      final Process process = processBuilder.start();
      exitCode = process.waitFor();
    } catch (final Throwable t) {
      if (exitCode == 0) {
        exitCode = Integer.MIN_VALUE;
      }
      LOGGER.log(Level.SEVERE, t.getMessage(), t);
    }

    return exitCode;
  }

  public List<String> getCommand() {
    return command;
  }

  public int getExitCode() {
    return exitCode;
  }

  public Path getProcessError() {
    return processError;
  }

  public Path getProcessOutput() {
    return processOutput;
  }

  public void setCommandLine(final List<String> args) {
    command = new ArrayList<>(args);
  }
}
