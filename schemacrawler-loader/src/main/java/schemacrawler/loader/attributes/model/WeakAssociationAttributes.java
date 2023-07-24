/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.loader.attributes.model;

import static java.util.Objects.requireNonNull;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

public class WeakAssociationAttributes extends ObjectAttributes {

  private static final long serialVersionUID = 8305929253225133307L;

  private final TableAttributes dependentTable;
  private final TableAttributes referencedTable;
  private final Map<String, String> columnReferences;

  @ConstructorProperties({"name", "remarks", "attributes", "referenced-table", "referencing-table",
      "column-references"})
  public WeakAssociationAttributes(final String name, final List<String> remarks,
      final Map<String, String> attributes, final TableAttributes referencedTable,
      final TableAttributes dependentTable, final Map<String, String> columnReferences) {
    super(name, remarks, attributes);
    this.referencedTable = requireNonNull(referencedTable, "No referenced table provided");
    this.dependentTable = requireNonNull(dependentTable, "No referencing table provided");
    if (columnReferences == null || columnReferences.isEmpty()) {
      throw new IllegalArgumentException("No column references provided");
    }
    this.columnReferences = columnReferences;
  }

  public Map<String, String> getColumnReferences() {
    return columnReferences;
  }

  public TableAttributes getReferencedTable() {
    return referencedTable;
  }

  public TableAttributes getDependentTable() {
    return dependentTable;
  }
}
