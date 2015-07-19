/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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
package schemacrawler.tools.options;


import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;

public enum InfoLevel
{

  unknown,
  minimum,
  standard,
  detailed,
  maximum, ;

  private static final Logger LOGGER = Logger.getLogger(InfoLevel.class
    .getName());

  public static InfoLevel valueOfFromString(final String infoLevelValue)
  {
    try
    {
      return InfoLevel.valueOf(infoLevelValue);
    }
    catch (final IllegalArgumentException | NullPointerException e)
    {
      LOGGER.log(Level.INFO, "Unknown infolevel, " + infoLevelValue);
      return unknown;
    }
  }

  public final SchemaInfoLevel buildSchemaInfoLevel()
  {
    final SchemaInfoLevel schemaInfoLevel;
    switch (this)
    {
      case minimum:
        schemaInfoLevel = SchemaInfoLevelBuilder.minimum();
        break;
      case standard:
        schemaInfoLevel = SchemaInfoLevelBuilder.standard();
        break;
      case detailed:
        schemaInfoLevel = SchemaInfoLevelBuilder.detailed();
        break;
      case maximum:
        schemaInfoLevel = SchemaInfoLevelBuilder.maximum();
        break;
      default:
        schemaInfoLevel = SchemaInfoLevelBuilder.standard();
        break;
    }
    schemaInfoLevel.setTag(name());
    return schemaInfoLevel;
  }

}
