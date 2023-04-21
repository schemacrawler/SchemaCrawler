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

package schemacrawler.schemacrawler;

import static schemacrawler.schemacrawler.SchemaInfoLevelBuilder.detailed;
import static schemacrawler.schemacrawler.SchemaInfoLevelBuilder.maximum;
import static schemacrawler.schemacrawler.SchemaInfoLevelBuilder.minimum;
import static schemacrawler.schemacrawler.SchemaInfoLevelBuilder.standard;

import java.util.function.Supplier;
import java.util.logging.Level;

import java.util.logging.Logger;
import us.fatehi.utility.string.StringFormat;

public enum InfoLevel {
  unknown(() -> standard()),
  minimum(() -> minimum()),
  standard(() -> standard()),
  detailed(() -> detailed()),
  maximum(() -> maximum());

  private static final Logger LOGGER = Logger.getLogger(InfoLevel.class.getName());

  public static InfoLevel valueOfFromString(final String infoLevelValue) {
    try {
      return InfoLevel.valueOf(infoLevelValue);
    } catch (final IllegalArgumentException | NullPointerException e) {
      LOGGER.log(Level.INFO, new StringFormat("Unknown infolevel <%s>", infoLevelValue));
      return unknown;
    }
  }

  private final Supplier<SchemaInfoLevel> toSchemaInfoLevelFunction;

  InfoLevel(final Supplier<SchemaInfoLevel> toSchemaInfoLevelFunction) {
    this.toSchemaInfoLevelFunction = toSchemaInfoLevelFunction;
  }

  public final SchemaInfoLevel toSchemaInfoLevel() {
    return toSchemaInfoLevelFunction.get();
  }
}
