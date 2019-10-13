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

package schemacrawler.test.serialize;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;
import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.integration.serialize.SerializationFormat;
import schemacrawler.tools.integration.serialize.SerializationLanguage;

public class SerializationLanguageTest
{

  @Test
  public void serializationLanguageByName()
    throws Exception
  {
    final SerializationLanguage serializationLanguage = new SerializationLanguage();
    for (final SerializationFormat serializationFormat : SerializationFormat.values())
    {
      final Config config = new Config();
      config.put("serialization-format", serializationFormat.name());
      serializationLanguage.addConfig(config);

      assertThat(serializationLanguage.getSerializationFormat(),
                 is(serializationFormat));
    }
  }

  @Test
  public void serializationLanguageForNull()
  {
    final SerializationLanguage serializationLanguage = new SerializationLanguage();
    final Config config = new Config();
    config.put("serialization-format", null);
    serializationLanguage.addConfig(config);

    assertThat(serializationLanguage.getSerializationFormat(),
               is(SerializationFormat.java));
  }

  @Test
  public void serializationLanguageForBlank()
  {
    final SerializationLanguage serializationLanguage = new SerializationLanguage();
    final Config config = new Config();
    config.put("serialization-format", "");
    serializationLanguage.addConfig(config);

    assertThat(serializationLanguage.getSerializationFormat(),
               is(SerializationFormat.java));
  }

  @Test
  public void serializationLanguageForBadValue()
  {
    final SerializationLanguage serializationLanguage = new SerializationLanguage();
    final Config config = new Config();
    config.put("serialization-format", "bad-value");
    serializationLanguage.addConfig(config);

    assertThat(serializationLanguage.getSerializationFormat(),
               is(SerializationFormat.unknown));
  }

}
