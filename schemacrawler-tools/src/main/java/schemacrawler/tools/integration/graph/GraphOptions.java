/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
package schemacrawler.tools.integration.graph;


import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;

public class GraphOptions
  extends SchemaTextOptions
{

  private static final long serialVersionUID = -5850945398335496207L;

  private SchemaTextDetailType schemaTextDetailType = SchemaTextDetailType.details;

  public GraphOptions()
  {
  }

  public GraphOptions(final Config config)
  {
    super(config);
  }

  public SchemaTextDetailType getSchemaTextDetailType()
  {
    return schemaTextDetailType;
  }

  public void setSchemaTextDetailType(final SchemaTextDetailType schemaTextDetailType)
  {
    if (schemaTextDetailType == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    this.schemaTextDetailType = schemaTextDetailType;
  }

}
