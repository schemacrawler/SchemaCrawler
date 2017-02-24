/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.options;


import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.schemacrawler.SchemaInfoLevelBuilder;
import sf.util.StringFormat;

public enum InfoLevel
{

 unknown,
 minimum,
 standard,
 detailed,
 maximum,;

  private static final Logger LOGGER = Logger
    .getLogger(InfoLevel.class.getName());

  public static InfoLevel valueOfFromString(final String infoLevelValue)
  {
    try
    {
      return InfoLevel.valueOf(infoLevelValue);
    }
    catch (final IllegalArgumentException | NullPointerException e)
    {
      LOGGER.log(Level.INFO,
                 new StringFormat("Unknown infolevel <%s>", infoLevelValue));
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
