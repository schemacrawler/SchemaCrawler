/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.tools.integration.graph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import schemacrawler.tools.text.schema.SchemaTextOptions;

public class GraphOptions
  extends SchemaTextOptions
{

  private static final long serialVersionUID = -5850945398335496207L;

  private List<String> graphvizOpts;
  private Map<String, String> graphvizAttributes;
  private boolean isShowForeignKeyCardinality;
  private boolean isShowPrimaryKeyCardinality;

  public GraphOptions()
  {
    graphvizOpts = new ArrayList<>();
    graphvizAttributes = makeDefaultGraphvizAttributes();
    isShowForeignKeyCardinality = true;
    isShowPrimaryKeyCardinality = true;
  }

  public Map<String, String> getGraphvizAttributes()
  {
    return graphvizAttributes;
  }

  public List<String> getGraphvizOpts()
  {
    return graphvizOpts;
  }

  public boolean isShowForeignKeyCardinality()
  {
    return isShowForeignKeyCardinality;
  }

  public boolean isShowPrimaryKeyCardinality()
  {
    return isShowPrimaryKeyCardinality;
  }

  public void setGraphvizAttributes(final Map<String, String> graphvizAttributes)
  {
    if (graphvizAttributes == null)
    {
      this.graphvizAttributes = makeDefaultGraphvizAttributes();
    }
    else
    {
      this.graphvizAttributes = graphvizAttributes;
    }
  }

  public void setGraphvizOpts(final List<String> graphvizOpts)
  {
    if (graphvizOpts == null)
    {
      this.graphvizOpts = new ArrayList<>();
    }
    else
    {
      this.graphvizOpts = graphvizOpts;
    }
  }

  public void setShowForeignKeyCardinality(final boolean isShowForeignKeyCardinality)
  {
    this.isShowForeignKeyCardinality = isShowForeignKeyCardinality;
  }

  public void setShowPrimaryKeyCardinality(final boolean isShowPrimaryKeyCardinality)
  {
    this.isShowPrimaryKeyCardinality = isShowPrimaryKeyCardinality;
  }

  private Map<String, String> makeDefaultGraphvizAttributes()
  {
    final Map<String, String> graphvizAttributes = new HashMap<>();

    final String GRAPH = "graph.";
    graphvizAttributes.put(GRAPH + "rankdir", "RL");
    graphvizAttributes.put(GRAPH + "labeljust", "r");
    graphvizAttributes.put(GRAPH + "fontname", "Helvetica");

    final String NODE = "node.";
    graphvizAttributes.put(NODE + "shape", "none");
    graphvizAttributes.put(NODE + "fontname", "Helvetica");

    final String EDGE = "edge.";
    graphvizAttributes.put(EDGE + "fontname", "Helvetica");

    return graphvizAttributes;
  }

}
