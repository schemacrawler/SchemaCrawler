/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.commandline.utility;

import static java.nio.charset.StandardCharsets.UTF_8;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.OutputOptionsBuilder;

public final class OutputOptionsConfig {

  private static final String SC_INPUT_ENCODING = "schemacrawler.encoding.input";
  private static final String SC_OUTPUT_ENCODING = "schemacrawler.encoding.output";

  public static OutputOptionsBuilder fromConfig(
      final OutputOptionsBuilder providedBuilder, final Config config) {
    final OutputOptionsBuilder builder;
    if (providedBuilder == null) {
      builder = OutputOptionsBuilder.builder();
    } else {
      builder = providedBuilder;
    }

    final Config configProperties;
    if (config == null) {
      configProperties = new Config();
    } else {
      configProperties = config;
    }

    builder
        .withInputEncoding(configProperties.getStringValue(SC_INPUT_ENCODING, UTF_8.name()))
        .withOutputEncoding(configProperties.getStringValue(SC_OUTPUT_ENCODING, UTF_8.name()));

    return builder;
  }
}
