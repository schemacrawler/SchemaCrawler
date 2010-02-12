/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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
package schemacrawler.tools.commandline;


import schemacrawler.schemacrawler.SchemaInfoLevel;

public enum InfoLevel
{

  minimum, standard, detailed, maximum, lint;

  public final SchemaInfoLevel getSchemaInfoLevel()
  {
    final SchemaInfoLevel schemaInfoLevel;
    switch (this)
    {
      case minimum:
        schemaInfoLevel = SchemaInfoLevel.minimum();
        break;
      case standard:
        schemaInfoLevel = SchemaInfoLevel.standard();
        break;
      case detailed:
        schemaInfoLevel = SchemaInfoLevel.detailed();
        break;
      case maximum:
        schemaInfoLevel = SchemaInfoLevel.maximum();
        break;
      case lint:
        schemaInfoLevel = SchemaInfoLevel.maximum();
        break;
      default:
        schemaInfoLevel = SchemaInfoLevel.standard();
        break;
    }
    return schemaInfoLevel;
  }

}
