/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.serialize;

import static java.nio.file.Files.newOutputStream;

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

/** Main executor for the serialization integration. */
public final class SerializationCommand extends BaseSchemaCrawlerCommand<SerializationOptions> {

  static final String COMMAND = "serialize";

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

      try (final OutputStream out = new GZIPOutputStream(newOutputStream(outputFile))) {
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
