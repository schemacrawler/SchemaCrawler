/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.tools.command.script;

import static java.util.Objects.requireNonNull;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.exceptions.ConfigurationException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.command.script.options.ScriptOptions;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.string.StringFormat;

/** Main executor for the script engine integration. */
public final class ScriptCommand extends BaseSchemaCrawlerCommand<ScriptOptions> {

  private static final Logger LOGGER = Logger.getLogger(ScriptCommand.class.getName());

  static final String COMMAND = "script";

  private ScriptExecutor scriptExecutor;

  public ScriptCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() {
    // Check availability of script
    commandOptions
        .getResource()
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
      final InputResource inputResource = commandOptions.getResource().get();
      final Reader reader = inputResource.openNewInputReader(inputCharset);
      final Writer writer = outputOptions.openNewOutputWriter();

      LOGGER.log(Level.CONFIG, new StringFormat("Evaluating script, %s", inputResource));

      // Set up the context
      final Map<String, Object> context = new HashMap<>();
      context.put("title", outputOptions.getTitle());
      context.put("catalog", catalog);
      context.put("connection", connection);
      context.put("chain", new CommandChain(this));

      scriptExecutor.initialize(context, reader, writer);
      scriptExecutor.run();
    } catch (final Exception e) {
      throw new InternalRuntimeException("Could not execute script", e);
    }
  }

  @Override
  public boolean usesConnection() {
    return true;
  }
}
