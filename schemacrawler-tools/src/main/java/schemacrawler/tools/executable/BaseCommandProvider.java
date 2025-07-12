/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.executable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptions;
import us.fatehi.utility.property.PropertyName;

public abstract class BaseCommandProvider implements CommandProvider {
  private final Collection<PropertyName> supportedCommands;

  public BaseCommandProvider(final Collection<PropertyName> supportedCommands) {
    this.supportedCommands = requireNonNull(supportedCommands, "No supported commands provided");
  }

  public BaseCommandProvider(final PropertyName command) {
    this(Arrays.asList(requireNonNull(command, "No command provided")));
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    return PluginCommand.empty();
  }

  @Override
  public final Collection<PropertyName> getSupportedCommands() {
    return new ArrayList<>(supportedCommands);
  }

  @Override
  public boolean supportsSchemaCrawlerCommand(
      final String command,
      final SchemaCrawlerOptions schemaCrawlerOptions,
      final Config additionalConfig,
      final OutputOptions outputOptions) {
    return supportsCommand(command);
  }

  protected final boolean supportsCommand(final String command) {
    return lookupSupportedCommand(command) != null;
  }

  protected final PropertyName lookupSupportedCommand(final String command) {
    if (isBlank(command)) {
      return null;
    }
    for (final PropertyName supportedCommand : supportedCommands) {
      if (supportedCommand != null && command.equalsIgnoreCase(supportedCommand.getName())) {
        return supportedCommand;
      }
    }
    return null;
  }

  protected boolean supportsOutputFormat(
      final String command,
      final OutputOptions outputOptions,
      final Predicate<String> outputFormatValuePredicate) {
    requireNonNull(outputFormatValuePredicate, "No output format value predicate provided");
    if (outputOptions == null) {
      return false;
    }
    final String format = outputOptions.getOutputFormatValue();
    if (isBlank(format)) {
      return false;
    }
    return outputFormatValuePredicate.test(format);
  }
}
