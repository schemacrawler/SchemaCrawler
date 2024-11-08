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
