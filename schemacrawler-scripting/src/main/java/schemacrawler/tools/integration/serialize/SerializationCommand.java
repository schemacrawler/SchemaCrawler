/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.integration.serialize;


import static java.nio.file.Files.newOutputStream;

import java.io.OutputStream;
import java.nio.file.Path;

import schemacrawler.schema.Catalog;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.options.OutputOptionsBuilder;

/**
 * Main executor for the serialization integration.
 *
 * @author Sualeh Fatehi
 */
public final class SerializationCommand
  extends BaseSchemaCrawlerCommand
{

  static final String COMMAND = "serialize";

  private final SerializationLanguage serializationLanguage;

  public SerializationCommand()
  {
    super(COMMAND);
    this.serializationLanguage = new SerializationLanguage();
  }

  @Override
  public void checkAvailability()
    throws Exception
  {
    // Nothing additional to check at this point. The Command should be
    // available after the class is loaded, and imports are resolved.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute()
    throws Exception
  {
    checkCatalog();

    serializationLanguage.addConfig(getAdditionalConfiguration());

    final SerializationFormat serializationFormat = serializationLanguage
      .getSerializationFormat();
    if (serializationFormat == SerializationFormat.unknown)
    {
      throw new SchemaCrawlerException("No serialization format provided");
    }
    final String serializerClassName = serializationFormat
      .getSerializerClassName();
    final Class<SerializableCatalog> serializableCatalogClass = (Class<SerializableCatalog>) Class
      .forName(serializerClassName);
    final SerializableCatalog serializableCatalog = serializableCatalogClass
      .getDeclaredConstructor(Catalog.class).newInstance(catalog);

    // Force a file to be created
    final Path outputFile = outputOptions
      .getOutputFile(serializationFormat.getFileExtension());

    outputOptions = OutputOptionsBuilder.builder(outputOptions)
      .withOutputFile(outputFile).toOptions();

    try (final OutputStream out = newOutputStream(outputFile))
    {
      serializableCatalog.save(out);
    }
  }

  @Override
  public boolean usesConnection()
  {
    return false;
  }

}
