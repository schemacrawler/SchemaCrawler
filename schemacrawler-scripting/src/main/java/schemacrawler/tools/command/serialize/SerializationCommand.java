/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.serialize;

import static java.nio.file.Files.newOutputStream;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;
import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.command.serialize.options.SerializationFormat;
import schemacrawler.tools.command.serialize.options.SerializationOptions;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.formatter.serialize.CatalogSerializer;
import schemacrawler.tools.options.OutputOptionsBuilder;
import us.fatehi.utility.property.PropertyName;

/** Main executor for the serialization integration. */
public final class SerializationCommand extends BaseSchemaCrawlerCommand<SerializationOptions> {

  static final PropertyName COMMAND =
      new PropertyName("serialize", "Create an offline catalog snapshot");

  public SerializationCommand() {
    super(COMMAND);
  }

  @Override
  public void checkAvailability() {
    // Nothing additional to check at this point. The Command should be
    // available after the class is loaded, and imports are resolved.
  }

  /** {@inheritDoc} */
  @Override
  public void execute() {
    checkCatalog();

    final SerializationFormat serializationFormat =
        SerializationFormat.fromFormat(outputOptions.getOutputFormatValue());

    final String serializerClassName = serializationFormat.getSerializerClassName();
    final CatalogSerializer catalogSerializer;
    try {
      final Class<CatalogSerializer> serializableCatalogClass =
          (Class<CatalogSerializer>) Class.forName(serializerClassName);
      catalogSerializer =
          serializableCatalogClass.getDeclaredConstructor(Catalog.class).newInstance(catalog);
    } catch (final Exception e) {
      throw new InternalRuntimeException(
          String.format("Could not instantiate catalog serializer<%s>", serializerClassName), e);
    }

    if (serializationFormat.isBinaryFormat()) {
      // Force a file to be created for binary formats such as Java serialization
      final Path outputFile = outputOptions.getOutputFile(serializationFormat.getFileExtension());

      outputOptions =
          OutputOptionsBuilder.builder(outputOptions).withOutputFile(outputFile).toOptions();

      try (final OutputStream out =
          new GZIPOutputStream(newOutputStream(outputFile, WRITE, CREATE, TRUNCATE_EXISTING))) {
        catalogSerializer.save(out);
      } catch (final IOException e) {
        throw new IORuntimeException("Could not save catalog", e);
      }
    } else {
      final Writer out = outputOptions.openNewOutputWriter();
      catalogSerializer.save(out);
      // NOTE: Jackson closes the output writer, so no need for a try-with-resources block
    }
  }
}
