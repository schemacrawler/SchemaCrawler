/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.tools.command.text.diagram.options;

import java.util.List;
import java.util.Map;

import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;

public final class DiagramOptions extends SchemaTextOptions {

  private final List<String> graphvizOpts;
  private final Map<String, String> graphvizAttributes;
  private final boolean isShowForeignKeyCardinality;
  private final boolean isShowPrimaryKeyCardinality;
  private final boolean isShowFilteredTables;

  protected DiagramOptions(final DiagramOptionsBuilder diagramOptionsBuilder) {
    super(diagramOptionsBuilder);

    graphvizOpts = diagramOptionsBuilder.graphvizOpts;
    graphvizAttributes = diagramOptionsBuilder.graphvizAttributes;
    isShowForeignKeyCardinality = diagramOptionsBuilder.isShowForeignKeyCardinality;
    isShowPrimaryKeyCardinality = diagramOptionsBuilder.isShowPrimaryKeyCardinality;
    isShowFilteredTables = diagramOptionsBuilder.isShowFilteredTables;
  }

  public Map<String, String> getGraphvizAttributes() {
    return graphvizAttributes;
  }

  public List<String> getGraphvizOpts() {
    return graphvizOpts;
  }

  public boolean isShowFilteredTables() {
    return isShowFilteredTables;
  }

  public boolean isShowForeignKeyCardinality() {
    return isShowForeignKeyCardinality;
  }

  public boolean isShowPrimaryKeyCardinality() {
    return isShowPrimaryKeyCardinality;
  }
}
