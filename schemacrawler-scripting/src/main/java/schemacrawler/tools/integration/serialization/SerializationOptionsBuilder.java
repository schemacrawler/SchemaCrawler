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


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.OptionsBuilder;

public final class SerializationOptionsBuilder
  implements OptionsBuilder<SerializationOptionsBuilder, SerializationOptions>
{

  private static final String CLI_SERIALIZATION_FORMAT = "serialization-format";
  private static final String SCHEMACRAWLER_SERIALIZATION_PREFIX = "schemacrawler.serialization.";
  private static final String SERIALIZATION_FORMAT =
    SCHEMACRAWLER_SERIALIZATION_PREFIX + CLI_SERIALIZATION_FORMAT;

  public static SerializationOptionsBuilder builder()
  {
    return new SerializationOptionsBuilder();
  }

  public static SerializationOptionsBuilder builder(final SerializationOptions options)
  {
    return new SerializationOptionsBuilder().fromOptions(options);
  }

  public static SerializationOptions newSerializationOptions()
  {
    return new SerializationOptionsBuilder().toOptions();
  }

  public static SerializationOptions newSerializationOptions(final Config config)
  {
    return new SerializationOptionsBuilder().fromConfig(config).toOptions();
  }

  SerializationFormat serializationFormat;

  private SerializationOptionsBuilder()
  {
    serializationFormat = SerializationFormat.java;
  }

  @Override
  public SerializationOptionsBuilder fromConfig(final Config map)
  {
    if (map == null)
    {
      return this;
    }

    final Config config = new Config(map);

    final String serializationFormatKey;
    if (config.containsKey(CLI_SERIALIZATION_FORMAT))
    {
      // Honor command-line option first
      serializationFormatKey = CLI_SERIALIZATION_FORMAT;
    }
    else
    {
      // Otherwise, take option from SchemaCrawler configuration file
      serializationFormatKey = SERIALIZATION_FORMAT;
    }
    serializationFormat = config
      .getEnumValue(serializationFormatKey, SerializationFormat.java);

    return this;
  }

  @Override
  public SerializationOptionsBuilder fromOptions(final SerializationOptions options)
  {
    if (options == null)
    {
      return this;
    }

    serializationFormat = options.getSerializationFormat();

    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = new Config();

    config.setEnumValue(SERIALIZATION_FORMAT, serializationFormat);

    return config;
  }

  @Override
  public SerializationOptions toOptions()
  {
    return new SerializationOptions(this);
  }

  /**
   * With a serialization format specified.
   */
  public SerializationOptionsBuilder withSerializationFormat(final SerializationFormat serializationFormat)
  {
    if (serializationFormat == null)
    {
      this.serializationFormat = SerializationFormat.java;
    }
    else
    {
      this.serializationFormat = serializationFormat;
    }
    return this;
  }

}
