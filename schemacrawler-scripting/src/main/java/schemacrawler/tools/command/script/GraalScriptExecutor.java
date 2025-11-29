/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.script;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.command.script.options.ScriptLanguageType;

/** Main executor for the Graal Polyglot integration. */
public final class GraalScriptExecutor implements ScriptExecutor {

  private static class WriterOutputStream extends OutputStream {
    private final Writer writer;

    public WriterOutputStream(final Writer writer) {
      this.writer = writer;
    }

    @Override
    public void close() throws IOException {
      writer.close();
    }

    @Override
    public void flush() throws IOException {
      writer.flush();
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
      final String str = new String(b, off, len, UTF_8);
      writer.write(str);
    }

    @Override
    public void write(final int b) throws IOException {
      writer.write(b);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(GraalScriptExecutor.class.getName());

  private final ScriptLanguageType scriptingLanguage;
  private Reader reader;
  private Writer writer;
  private Map<String, Object> context;

  public GraalScriptExecutor(final ScriptLanguageType scriptingLanguage) {
    this.scriptingLanguage = requireNonNull(scriptingLanguage, "No scripting language provided");
    if (scriptingLanguage == ScriptLanguageType.unknown) {
      throw new IllegalArgumentException("Unknown scripting language");
    }
  }

  @Override
  public boolean canGenerate() {
    final String language = scriptingLanguage.name();
    try (final Context graalPolyglotContext =
        Context.newBuilder(language).allowAllAccess(true).build()) {
      // Do a lightweight eval to trigger language initialization
      graalPolyglotContext.eval(language, "1");
      return true;
    } catch (final Exception e) {
      return false;
    }
  }

  @Override
  public void initialize(
      final Map<String, Object> context, final Reader reader, final Writer writer) {

    this.reader = requireNonNull(reader, "No script input resource provided");
    this.writer = requireNonNull(writer, "No output writer provided");

    if (context == null) {
      this.context = Collections.emptyMap();
    } else {
      this.context = new HashMap<>(context);
    }
  }

  @Override
  public void run() {
    final String language = scriptingLanguage.name();
    LOGGER.log(Level.INFO, "Executing %s script".formatted(language));
    try (final Context graalPolyglotContext =
        Context.newBuilder(language)
            .allowAllAccess(true)
            .out(new WriterOutputStream(writer))
            .build()) {
      // Get the script source
      final Source sourceCode =
          Source.newBuilder(language, reader, "schemacrawler_script_execution").build();

      // Set execution content
      final Value bindings = graalPolyglotContext.getBindings(language);
      for (final Entry<String, Object> entry : context.entrySet()) {
        bindings.putMember(entry.getKey(), entry.getValue());
      }

      // Evaluate the script
      graalPolyglotContext.eval(sourceCode);
    } catch (final IOException e) {
      throw new IORuntimeException(e.getMessage(), e);
    }
  }
}
