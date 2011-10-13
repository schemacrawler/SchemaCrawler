/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools.integration.serialization;


import java.io.Writer;
import java.sql.Connection;
import java.util.logging.Logger;

import schemacrawler.schema.Database;
import schemacrawler.tools.executable.BaseExecutable;
import schemacrawler.tools.options.OutputWriter;
import sf.util.Utility;

/**
 * Main executor for the graphing integration.
 * 
 * @author Sualeh Fatehi
 */
public final class SerializationExecutable
  extends BaseExecutable
{

  enum OutputFormat
  {
    xml, json, ;
  }

  private static final Logger LOGGER = Logger
    .getLogger(SerializationExecutable.class.getName());

  public SerializationExecutable()
  {
    this(null);
  }

  public SerializationExecutable(final String command)
  {
    super(command);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeOn(final Database db, final Connection connection)
    throws Exception
  {
    OutputFormat outputFormat;
    final String outputFormatString = outputOptions.getOutputFormatValue();
    if (Utility.isBlank(outputFormatString))
    {
      outputFormat = OutputFormat.xml;
    }
    else
    {
      try
      {
        outputFormat = OutputFormat.valueOf(outputFormatString);
      }
      catch (final IllegalArgumentException e)
      {
        outputFormat = OutputFormat.xml;
      }
    }

    final Writer writer = new OutputWriter(outputOptions);
    final SerializableDatabase database;
    switch (outputFormat)
    {
      case xml:
        database = new XmlDatabase(db);
        break;

      default:
        database = new XmlDatabase(db);
        break;
    }
    database.save(writer);
    writer.close();
  }

}
