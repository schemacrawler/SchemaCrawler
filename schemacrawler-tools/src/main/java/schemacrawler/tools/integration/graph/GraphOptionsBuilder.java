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


import static sf.util.Utility.join;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.schema.SchemaTextDetailType;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.Utility;

public class GraphOptionsBuilder
  extends SchemaTextOptionsBuilder
{

  private static final String GRAPH_SHOW_PRIMARY_KEY_CARDINALITY = "schemacrawler.graph.show.primarykey.cardinality";
  private static final String GRAPH_SHOW_FOREIGN_KEY_CARDINALITY = "schemacrawler.graph.show.foreignkey.cardinality";
  private static final String GRAPH_DETAILS = "schemacrawler.graph.details";
  private static final String GRAPH_GRAPHVIZ_OPTS = "schemacrawler.graph.graphviz_opts";
  private static final String SC_GRAPHVIZ_OPTS = "SC_GRAPHVIZ_OPTS";

  private static final Logger LOGGER = Logger
    .getLogger(GraphOptions.class.getName());

  public GraphOptionsBuilder()
  {
    this(new GraphOptions());
  }

  public GraphOptionsBuilder(final GraphOptions options)
  {
    super(options);
  }

  @Override
  public GraphOptionsBuilder fromConfig(final Map<String, String> map)
  {
    if (map == null)
    {
      return this;
    }
    super.fromConfig(map);

    final Config config = new Config(map);

    final GraphOptions options = (GraphOptions) this.options;

    options.setShowPrimaryKeyCardinality(config
      .getBooleanValue(GRAPH_SHOW_PRIMARY_KEY_CARDINALITY, true));
    options.setShowForeignKeyCardinality(config
      .getBooleanValue(GRAPH_SHOW_FOREIGN_KEY_CARDINALITY, true));

    options.setSchemaTextDetailType(config
      .getEnumValue(GRAPH_DETAILS, SchemaTextDetailType.details));

    options.setGraphVizOpts(listGraphVizOpts(readGraphVizOpts(config)));

    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = super.toConfig();

    final GraphOptions options = (GraphOptions) this.options;

    config.setBooleanValue(GRAPH_SHOW_PRIMARY_KEY_CARDINALITY,
                           options.isShowPrimaryKeyCardinality());
    config.setBooleanValue(GRAPH_SHOW_FOREIGN_KEY_CARDINALITY,
                           options.isShowForeignKeyCardinality());

    config.setEnumValue(GRAPH_DETAILS, options.getSchemaTextDetailType());

    config.setStringValue(GRAPH_GRAPHVIZ_OPTS,
                          join(options.getGraphVizOpts(), " "));

    return config;
  }

  @Override
  public GraphOptions toOptions()
  {
    return (GraphOptions) super.toOptions();
  }

  private List<String> listGraphVizOpts(final String graphVizOptions)
  {
    final List<String> graphVizOptionsList = Arrays
      .asList(graphVizOptions.split("\\s+"));
    return graphVizOptionsList;
  }

  private String readGraphVizOpts(final Config config)
  {
    final String scGraphVizOptsCfg = config.getStringValue(GRAPH_GRAPHVIZ_OPTS,
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
      LOGGER.log(Level.CONFIG,
                 "Using additional GraphViz command-line options from SC_GRAPHVIZ_OPTS system property, "
                               + scGraphVizOptsProp);
      return scGraphVizOptsProp;
    }

    final String scGraphVizOptsEnv = System.getenv(SC_GRAPHVIZ_OPTS);
    if (!Utility.isBlank(scGraphVizOptsEnv))
    {
      LOGGER.log(Level.CONFIG,
                 "Using additional GraphViz command-line options from SC_GRAPHVIZ_OPTS environmental variable, "
                               + scGraphVizOptsEnv);
      return scGraphVizOptsEnv;
    }

    return "";
  }

}
