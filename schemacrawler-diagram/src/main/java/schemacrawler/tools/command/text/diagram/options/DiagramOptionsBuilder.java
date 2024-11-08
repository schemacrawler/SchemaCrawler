/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.text.diagram.options;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.join;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.tools.command.text.schema.options.BaseSchemaTextOptionsBuilder;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.PropertiesUtility;
import us.fatehi.utility.string.StringFormat;

public final class DiagramOptionsBuilder
    extends BaseSchemaTextOptionsBuilder<DiagramOptionsBuilder, DiagramOptions> {

  protected static final String SCHEMACRAWLER_GRAPH_PREFIX = "schemacrawler.graph.";

  private static final String GRAPH_SHOW_PRIMARY_KEY_CARDINALITY =
      SCHEMACRAWLER_GRAPH_PREFIX + "show.primarykey.cardinality";
  private static final String GRAPH_SHOW_FOREIGN_KEY_CARDINALITY =
      SCHEMACRAWLER_GRAPH_PREFIX + "show.foreignkey.cardinality";
  private static final String GRAPH_SHOW_FOREIGN_KEY_FILTERED_TABLES =
      SCHEMACRAWLER_GRAPH_PREFIX + "show.foreignkey.filtered_tables";
  private static final String GRAPH_GRAPHVIZ_OPTS = SCHEMACRAWLER_GRAPH_PREFIX + "graphviz_opts";
  private static final String SC_GRAPHVIZ_OPTS = "SC_GRAPHVIZ_OPTS";
  private static final String GRAPH_GRAPHVIZ_ATTRIBUTES = SCHEMACRAWLER_GRAPH_PREFIX + "graphviz";

  private static final Logger LOGGER = Logger.getLogger(DiagramOptions.class.getName());

  public static DiagramOptionsBuilder builder() {
    return new DiagramOptionsBuilder();
  }

  public static DiagramOptionsBuilder builder(final DiagramOptions options) {
    return new DiagramOptionsBuilder().fromOptions(options);
  }

  private static Map<String, String> makeDefaultGraphvizAttributes() {
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

  protected List<String> graphvizOpts;
  protected Map<String, String> graphvizAttributes;
  protected boolean isShowForeignKeyCardinality;
  protected boolean isShowPrimaryKeyCardinality;
  protected boolean isShowFilteredTables;

  private DiagramOptionsBuilder() {
    // Default values
    graphvizOpts = new ArrayList<>();
    graphvizAttributes = makeDefaultGraphvizAttributes();
    isShowForeignKeyCardinality = true;
    isShowPrimaryKeyCardinality = true;
    isShowFilteredTables = true;
  }

  @Override
  public DiagramOptionsBuilder fromConfig(final Config config) {
    if (config == null) {
      return this;
    }
    super.fromConfig(config);

    isShowPrimaryKeyCardinality = config.getBooleanValue(GRAPH_SHOW_PRIMARY_KEY_CARDINALITY, true);
    isShowForeignKeyCardinality = config.getBooleanValue(GRAPH_SHOW_FOREIGN_KEY_CARDINALITY, true);
    isShowFilteredTables = config.getBooleanValue(GRAPH_SHOW_FOREIGN_KEY_FILTERED_TABLES, true);

    graphvizOpts = listGraphvizOpts(readGraphvizOpts(config));

    final Map<String, String> graphvizAttributes = readGraphvizAttributes(config);
    if (graphvizAttributes != null) {
      this.graphvizAttributes = graphvizAttributes;
    }

    return this;
  }

  @Override
  public DiagramOptionsBuilder fromOptions(final DiagramOptions options) {
    if (options == null) {
      return this;
    }
    super.fromOptions(options);

    isShowPrimaryKeyCardinality = options.isShowPrimaryKeyCardinality();
    isShowForeignKeyCardinality = options.isShowForeignKeyCardinality();
    isShowFilteredTables = options.isShowFilteredTables();

    graphvizOpts = options.getGraphvizOpts();
    graphvizAttributes = options.getGraphvizAttributes();

    return this;
  }

  public DiagramOptionsBuilder showFilteredTables() {
    return showFilteredTables(true);
  }

  public DiagramOptionsBuilder showFilteredTables(final boolean value) {
    isShowFilteredTables = value;
    return this;
  }

  public DiagramOptionsBuilder showForeignKeyCardinality() {
    return showForeignKeyCardinality(true);
  }

  public DiagramOptionsBuilder showForeignKeyCardinality(final boolean value) {
    isShowForeignKeyCardinality = value;
    return this;
  }

  public DiagramOptionsBuilder showPrimaryKeyCardinality() {
    return showPrimaryKeyCardinality(true);
  }

  public DiagramOptionsBuilder showPrimaryKeyCardinality(final boolean value) {
    isShowPrimaryKeyCardinality = value;
    return this;
  }

  @Override
  public Config toConfig() {
    final Config config = super.toConfig();

    config.put(GRAPH_SHOW_PRIMARY_KEY_CARDINALITY, isShowPrimaryKeyCardinality);
    config.put(GRAPH_SHOW_FOREIGN_KEY_CARDINALITY, isShowForeignKeyCardinality);
    config.put(GRAPH_SHOW_FOREIGN_KEY_FILTERED_TABLES, isShowFilteredTables);

    config.put(GRAPH_GRAPHVIZ_OPTS, join(graphvizOpts, " "));

    graphvizAttributesToConfig(graphvizAttributes, config);

    return config;
  }

  @Override
  public DiagramOptions toOptions() {
    return new DiagramOptions(this);
  }

  public DiagramOptionsBuilder withGraphvizAttributes(
      final Map<String, String> graphvizAttributes) {
    if (graphvizAttributes == null) {
      this.graphvizAttributes = makeDefaultGraphvizAttributes();
    } else {
      this.graphvizAttributes = graphvizAttributes;
    }
    return this;
  }

  public DiagramOptionsBuilder withGraphvizOpts(final List<String> graphvizOpts) {
    if (graphvizOpts == null) {
      this.graphvizOpts = new ArrayList<>();
    } else {
      this.graphvizOpts = graphvizOpts;
    }
    return this;
  }

  private void graphvizAttributesToConfig(
      final Map<String, String> graphvizAttributes, final Config config) {
    for (final Entry<String, String> graphvizAttribute : graphvizAttributes.entrySet()) {
      final String fullKey =
          String.format("%s.%s", GRAPH_GRAPHVIZ_ATTRIBUTES, graphvizAttribute.getKey());
      final String value = graphvizAttribute.getValue();
      config.put(fullKey, value);
    }
  }

  private List<String> listGraphvizOpts(final String graphVizOptions) {
    if (isBlank(graphVizOptions)) {
      return new ArrayList<>();
    }

    final List<String> graphVizOptionsList = Arrays.asList(graphVizOptions.split("\\s+"));
    return graphVizOptionsList;
  }

  private Map<String, String> readGraphvizAttributes(final Config config) {
    if (config == null) {
      return null;
    }

    final Map<String, Object> subMap = config.getSubMap(GRAPH_GRAPHVIZ_ATTRIBUTES);

    final Map<String, String> graphvizAttributes = new HashMap<>();
    for (final Entry<String, Object> subMapEntry : subMap.entrySet()) {
      final String key = subMapEntry.getKey();
      final String value = String.valueOf(subMapEntry.getValue());
      graphvizAttributes.put(key, value);
    }

    if (graphvizAttributes.isEmpty()) {
      return null;
    }

    return graphvizAttributes;
  }

  private String readGraphvizOpts(final Config config) {
    final String scGraphvizOptsCfg = config.getStringValue(GRAPH_GRAPHVIZ_OPTS, "");
    if (!isBlank(scGraphvizOptsCfg)) {
      LOGGER.log(
          Level.CONFIG,
          new StringFormat(
              "Using additional Graphviz command-line options from config <%s>",
              scGraphvizOptsCfg));
      return scGraphvizOptsCfg;
    }

    return PropertiesUtility.getSystemConfigurationProperty(SC_GRAPHVIZ_OPTS, "");
  }
}
