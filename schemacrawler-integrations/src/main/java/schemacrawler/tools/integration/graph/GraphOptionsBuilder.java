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


import static sf.util.Utility.isBlank;
import static sf.util.Utility.join;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.text.schema.SchemaTextOptionsBuilder;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

public class GraphOptionsBuilder
  extends SchemaTextOptionsBuilder
{

  private static final String GRAPH_SHOW_PRIMARY_KEY_CARDINALITY = "schemacrawler.graph.show.primarykey.cardinality";
  private static final String GRAPH_SHOW_FOREIGN_KEY_CARDINALITY = "schemacrawler.graph.show.foreignkey.cardinality";
  private static final String GRAPH_GRAPHVIZ_OPTS = "schemacrawler.graph.graphviz_opts";
  private static final String SC_GRAPHVIZ_OPTS = "SC_GRAPHVIZ_OPTS";
  private static final String GRAPH_GRAPHVIZ_ATTRIBUTES = "schemacrawler.graph.graphviz";

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
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
  public GraphOptionsBuilder fromConfig(final Config map)
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

    options.setGraphvizOpts(listGraphvizOpts(readGraphvizOpts(config)));
    options.setGraphvizAttributes(readGraphvizAttributes(config));

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

    config.setStringValue(GRAPH_GRAPHVIZ_OPTS,
                          join(options.getGraphvizOpts(), " "));

    graphvizAttributesToConfig(options.getGraphvizAttributes(), config);

    return config;
  }

  @Override
  public GraphOptions toOptions()
  {
    return (GraphOptions) super.toOptions();
  }

  private void graphvizAttributesToConfig(final Map<String, String> graphvizAttributes,
                                          final Config config)
  {
    for (final Entry<String, String> graphvizAttribute: graphvizAttributes
      .entrySet())
    {
      final String fullKey = String
        .format("%s.%s", GRAPH_GRAPHVIZ_ATTRIBUTES, graphvizAttribute.getKey());
      final String value = graphvizAttribute.getValue();
      config.put(fullKey, value);
    }
  }

  private List<String> listGraphvizOpts(final String graphVizOptions)
  {
    final List<String> graphVizOptionsList = Arrays
      .asList(graphVizOptions.split("\\s+"));
    return graphVizOptionsList;
  }

  private Map<String, String> readGraphvizAttributes(final Config config)
  {
    final Map<String, String> graphvizAttributes = new HashMap<>();
    for (final Entry<String, String> configEntry: config.entrySet())
    {
      final String fullKey = configEntry.getKey();
      if (fullKey == null || !fullKey.startsWith(GRAPH_GRAPHVIZ_ATTRIBUTES))
      {
        continue;
      }

      final String key = fullKey
        .substring(GRAPH_GRAPHVIZ_ATTRIBUTES.length() + 1);
      final String value = configEntry.getValue();
      graphvizAttributes.put(key, value);
    }

    if (graphvizAttributes.isEmpty())
    {
      return null;
    }

    return graphvizAttributes;
  }

  private String readGraphvizOpts(final Config config)
  {
    final String scGraphvizOptsCfg = config.getStringValue(GRAPH_GRAPHVIZ_OPTS,
                                                           "");
    if (!isBlank(scGraphvizOptsCfg))
    {
      LOGGER
        .log(Level.CONFIG,
             new StringFormat("Using additional Graphviz command-line options from config <%s>",
                              scGraphvizOptsCfg));
      return scGraphvizOptsCfg;
    }

    final String scGraphvizOptsProp = System.getProperty(SC_GRAPHVIZ_OPTS);
    if (!isBlank(scGraphvizOptsProp))
    {
      LOGGER
        .log(Level.CONFIG,
             new StringFormat("Using additional Graphviz command-line options from SC_GRAPHVIZ_OPTS system property <%s>",
                              scGraphvizOptsProp));
      return scGraphvizOptsProp;
    }

    final String scGraphvizOptsEnv = System.getenv(SC_GRAPHVIZ_OPTS);
    if (!isBlank(scGraphvizOptsEnv))
    {
      LOGGER
        .log(Level.CONFIG,
             new StringFormat("Using additional Graphviz command-line options from SC_GRAPHVIZ_OPTS environmental variable <%s>",
                              scGraphvizOptsEnv));
      return scGraphvizOptsEnv;
    }

    return "";
  }

}
