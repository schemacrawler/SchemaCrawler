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
package schemacrawler.tools.integration.graph;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;

public class GraphOptions
  extends SchemaTextOptions
{

  private static final long serialVersionUID = -5850945398335496207L;

  private List<String> graphVizOpts;
  private SchemaTextDetailType schemaTextDetailType;
  private boolean isShowForeignKeyCardinality;
  private boolean isShowPrimaryKeyCardinality;

  public GraphOptions()
  {
    graphVizOpts = new ArrayList<>();
    schemaTextDetailType = SchemaTextDetailType.details;
    isShowForeignKeyCardinality = true;
    isShowPrimaryKeyCardinality = true;
  }

  public List<String> getGraphVizOpts()
  {
    return graphVizOpts;
  }

  public SchemaTextDetailType getSchemaTextDetailType()
  {
    return schemaTextDetailType;
  }

  public boolean isShowForeignKeyCardinality()
  {
    return isShowForeignKeyCardinality;
  }

  public boolean isShowPrimaryKeyCardinality()
  {
    return isShowPrimaryKeyCardinality;
  }

  public void setGraphVizOpts(final List<String> graphVizOpts)
  {
    if (graphVizOpts == null)
    {
      this.graphVizOpts = new ArrayList<>();
    }
    else
    {
      this.graphVizOpts = graphVizOpts;
    }
  }

  public void setSchemaTextDetailType(final SchemaTextDetailType schemaTextDetailType)
  {
    if (schemaTextDetailType == null)
    {
      return;
    }
    this.schemaTextDetailType = schemaTextDetailType;
  }

  public void setShowForeignKeyCardinality(final boolean isShowForeignKeyCardinality)
  {
    this.isShowForeignKeyCardinality = isShowForeignKeyCardinality;
  }

  public void setShowPrimaryKeyCardinality(final boolean isShowPrimaryKeyCardinality)
  {
    this.isShowPrimaryKeyCardinality = isShowPrimaryKeyCardinality;
  }

}
