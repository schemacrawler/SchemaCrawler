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


import static java.util.Objects.requireNonNull;

import schemacrawler.schemacrawler.Options;

public class SerializationOptions
  implements Options
{

  private final SerializationFormat serializationFormat;

  public SerializationOptions(final SerializationOptionsBuilder builder)
  {
    requireNonNull(builder, "No builder provided");

    serializationFormat = builder.serializationFormat;
  }

  /**
   * Gets the configured catalog serialization format.
   * @return Serialization format
   */
  public SerializationFormat getSerializationFormat()
  {
    return serializationFormat;
  }

}
