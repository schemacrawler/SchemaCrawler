/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.script;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.command.script.options.ScriptOptions;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.registry.ScriptEngineRegistry;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/** Main executor for the script engine integration. */
public final class ScriptCommand extends BaseSchemaCrawlerCommand<ScriptOptions> {

  private static final Logger LOGGER = Logger.getLogger(ScriptCommand.class.getName());

  static final PropertyName COMMAND =
      new PropertyName(
          "script", "Process a script file, such as JavaScript, against the database schema");

  private ScriptExecutor scriptExecutor;

  public ScriptCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() {

    ScriptEngineRegistry.getScriptEngineRegistry().log(); // Will log

    // Check availability of script
    commandOptions
        .getInputResource()
        .orElseThrow(
            () ->
                new ConfigurationException(
                    String.format("Script not found <%s>", commandOptions.getScript())));

    final String scriptingLanguage = commandOptions.getLanguage();

    // Attempt to use Graal JavaScript
    scriptExecutor = new GraalJSScriptExecutor(scriptingLanguage);
    if (scriptExecutor.canGenerate()) {
      LOGGER.log(Level.CONFIG, "Loaded JavaScript executor using Graal JavaScript");
      return;
    }

    // Attempt to use a script engine
    scriptExecutor = new ScriptEngineExecutor(scriptingLanguage);
    if (scriptExecutor.canGenerate()) {
      LOGGER.log(
          Level.CONFIG,
          new StringFormat("Loaded <%s> executor using script engine", scriptingLanguage));
      return;
    }

    // No suitable engine found
    throw new InternalRuntimeException(
        "Scripting engine not found for language, " + scriptingLanguage);
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    requireNonNull(commandOptions, "No script language provided");
    checkCatalog();

    requireNonNull(scriptExecutor, "Scripting engine not found");
    try {
      final Charset inputCharset = outputOptions.getInputCharset();
      final InputResource inputResource = commandOptions.getInputResource().get();
      try (final Reader reader = inputResource.openNewInputReader(inputCharset);
          final Writer writer = outputOptions.openNewOutputWriter(); ) {

        LOGGER.log(Level.CONFIG, new StringFormat("Evaluating script, %s", inputResource));

        // Set up the context
        final Map<String, Object> context = new HashMap<>();
        context.put("title", outputOptions.getTitle());
        context.put("catalog", catalog);
        context.put("connection", connection);
        context.put("chain", new CommandChain(this));

        scriptExecutor.initialize(context, reader, writer);
        scriptExecutor.run();
      }
    } catch (final Exception e) {
      throw new InternalRuntimeException("Could not execute script", e);
    }
  }

  @Override
  public boolean usesConnection() {
    return true;
  }
}
