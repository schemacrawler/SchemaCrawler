/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.io.Writer;
import java.sql.Connection;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.executable.BaseStagedExecutable;

/**
 * Main executor for the graphing integration.
 *
 * @author Sualeh Fatehi
 */
public final class SerializationExecutable
  extends BaseStagedExecutable
{

  static final String COMMAND = "serialize";

  public SerializationExecutable()
  {
    this(COMMAND);
  }

  public SerializationExecutable(final String command)
  {
    super(command);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void executeOn(final Catalog db, final Connection connection)
    throws Exception
  {
    final SerializableCatalog catalog = new XmlSerializedCatalog(db);
    outputOptions.forceCompressedOutputFile();
    try (final Writer writer = outputOptions.openNewOutputWriter();)
    {
      catalog.save(writer);
    }
  }

}
