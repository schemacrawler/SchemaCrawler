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


import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptions;
import sf.util.Utility;

public class GraphOptions
  extends SchemaTextOptions
{

  private static final long serialVersionUID = -5850945398335496207L;

  private static final String GRAPH_DETAILS = "schemacrawler.graph.details";
  private static final String GRAPH_GRAPHVIZ_OPTS = "schemacrawler.graph.graphviz_opts";
  private static final String SC_GRAPHVIZ_OPTS = "SC_GRAPHVIZ_OPTS";

  private static final Logger LOGGER = Logger.getLogger(GraphOptions.class
    .getName());

  public GraphOptions()
  {
    // Required to set all options
    this(null);
  }

  public GraphOptions(final Config config)
  {
    super(config);

    setSchemaTextDetailType(getEnumValue(config,
                                         GRAPH_DETAILS,
                                         SchemaTextDetailType.details));

    setGraphVizOpts(readGraphVizOpts(config));
  }

  public String getGraphVizOpts()
  {
    return getStringValue(GRAPH_GRAPHVIZ_OPTS, "");
  }

  public SchemaTextDetailType getSchemaTextDetailType()
  {
    return getEnumValue(GRAPH_DETAILS, SchemaTextDetailType.details);
  }

  public void setGraphVizOpts(final String graphVizOpts)
  {
    if (graphVizOpts == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    setStringValue(GRAPH_GRAPHVIZ_OPTS, graphVizOpts);
  }

  public void setSchemaTextDetailType(final SchemaTextDetailType schemaTextDetailType)
  {
    if (schemaTextDetailType == null)
    {
      throw new IllegalArgumentException("Cannot use null value in a setter");
    }
    setEnumValue(GRAPH_DETAILS, schemaTextDetailType);
  }

  private String readGraphVizOpts(final Config config)
  {
    final String scGraphVizOptsCfg = getStringValue(config,
                                                    GRAPH_GRAPHVIZ_OPTS,
                                                    "");
    if (!Utility.isBlank(scGraphVizOptsCfg))
    {
      LOGGER.log(Level.CONFIG,
                 "Using additional GraphViz command-line options from config, "
                     + scGraphVizOptsCfg);
      return scGraphVizOptsCfg;
    }

    final String scGraphVizOptsProp = System.getProperty(SC_GRAPHVIZ_OPTS);
    if (!Utility.isBlank(scGraphVizOptsProp))
    {
      LOGGER
        .log(Level.CONFIG,
             "Using additional GraphViz command-line options from SC_GRAPHVIZ_OPTS system property, "
                 + scGraphVizOptsProp);
      return scGraphVizOptsProp;
    }

    final String scGraphVizOptsEnv = System.getenv(SC_GRAPHVIZ_OPTS);
    if (!Utility.isBlank(scGraphVizOptsEnv))
    {
      LOGGER
        .log(Level.CONFIG,
             "Using additional GraphViz command-line options from SC_GRAPHVIZ_OPTS environmental variable, "
                 + scGraphVizOptsEnv);
      return scGraphVizOptsEnv;
    }

    return "";
  }

}
