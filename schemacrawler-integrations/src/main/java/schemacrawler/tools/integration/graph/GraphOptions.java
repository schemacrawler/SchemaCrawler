/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.util.List;
import java.util.Map;

import schemacrawler.tools.text.schema.BaseSchemaTextOptions;

public class GraphOptions
  extends BaseSchemaTextOptions
{

  private final List<String> graphvizOpts;
  private final Map<String, String> graphvizAttributes;
  private final boolean isShowForeignKeyCardinality;
  private final boolean isShowPrimaryKeyCardinality;

  protected GraphOptions(final GraphOptionsBuilder graphOptionsBuilder)
  {
    super(graphOptionsBuilder);

    graphvizOpts = graphOptionsBuilder.graphvizOpts;
    graphvizAttributes = graphOptionsBuilder.graphvizAttributes;
    isShowForeignKeyCardinality = graphOptionsBuilder.isShowForeignKeyCardinality;
    isShowPrimaryKeyCardinality = graphOptionsBuilder.isShowPrimaryKeyCardinality;
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

}
