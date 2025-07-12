/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
