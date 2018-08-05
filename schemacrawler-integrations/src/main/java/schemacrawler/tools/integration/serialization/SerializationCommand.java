/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.IOException;
import java.io.Writer;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.options.OutputOptions;
import schemacrawler.tools.options.OutputOptionsBuilder;
import sf.util.SchemaCrawlerLogger;
import sf.util.Utility;

/**
 * Main executor for the graphing integration.
 *
 * @author Sualeh Fatehi
 */
public final class SerializationCommand
  extends BaseSchemaCrawlerCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(SerializationCommand.class.getName());

  static final String COMMAND = "serialize";

  public SerializationCommand()
  {
    this(COMMAND);
  }

  public SerializationCommand(final String command)
  {
    super(command);
  }

  @Override
  public void checkAvailibility()
    throws Exception
  {
    if (!Utility.isClassAvailable("com.thoughtworks.xstream.XStream"))
    {
      throw new SchemaCrawlerException("Cannot use offline databases");
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void execute()
    throws Exception
  {
    checkCatalog();

    final SerializableCatalog serializableCatalog = new XmlSerializedCatalog(catalog);
    // Force output to a compressed file
    outputOptions = forceCompressedFileOutput();
    try (final Writer writer = outputOptions.openNewOutputWriter();)
    {
      serializableCatalog.save(writer);
    }
  }

  private OutputOptions forceCompressedFileOutput()
    throws IOException
  {
    return OutputOptionsBuilder.builder(outputOptions)
      .withCompressedOutputFile(outputOptions.getOutputFile()).toOptions();
  }

}
