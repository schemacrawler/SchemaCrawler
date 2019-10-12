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

package schemacrawler.tools.integration.serialization;


import static java.nio.file.Files.newOutputStream;
import static java.util.Objects.requireNonNull;

import java.io.OutputStream;
import java.nio.file.Path;

import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.options.OutputOptionsBuilder;

/**
 * Main executor for the graphing integration.
 *
 * @author Sualeh Fatehi
 */
public final class SerializationCommand
  extends BaseSchemaCrawlerCommand
{

  static final String COMMAND = "serialize";

  private SerializationOptions serializationOptions;

  public SerializationCommand()
  {
    this(COMMAND);
  }

  public SerializationCommand(final String command)
  {
    super(command);
  }

  public final void setSerializationOptions(final SerializationOptions serializationOptions)
  {
    this.serializationOptions = requireNonNull(serializationOptions,
                                               "No serialization options provided");
  }

  @Override
  public void checkAvailability()
    throws Exception
  {
    // Nothing additional to check at this point. The Command should be
    // available after the class is loaded, and imports are resolved.
  }

  @Override
  public void initialize()
    throws Exception
  {
    super.initialize();
    loadSerializationOptions();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute()
    throws Exception
  {
    checkCatalog();

    final SerializationFormat serializationFormat = serializationOptions
      .getSerializationFormat();

    // Force a file to be created
    final Path outputFile = outputOptions
      .getOutputFile(serializationFormat.getFileExtension());

    outputOptions = OutputOptionsBuilder.builder(outputOptions)
      .withOutputFile(outputFile).toOptions();

    final SerializableCatalog serializableCatalog = serializationFormat
      .getSerializableCatalog(catalog);
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

  private void loadSerializationOptions()
  {
    if (serializationOptions == null)
    {
      serializationOptions = SerializationOptionsBuilder.builder()
        .fromConfig(additionalConfiguration).toOptions();
    }
  }

}
