/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import schemacrawler.schema.Schema;
import schemacrawler.schemacrawler.SchemaReference;

public class TableAttributes extends ObjectAttributes implements Iterable<ColumnAttributes> {

  private static final long serialVersionUID = -3510286847668145323L;

  private final String schemaName;
  private final String catalogName;
  private final Set<ColumnAttributes> columns;

  @ConstructorProperties({"schema", "catalog", "name", "remarks", "attributes", "columns"})
  public TableAttributes(
      final String schemaName,
      final String catalogName,
      final String name,
      final List<String> remarks,
      final Map<String, String> attributes,
      final Set<ColumnAttributes> columns) {
    super(name, remarks, attributes);
    this.catalogName = catalogName;
    this.schemaName = schemaName;
    if (columns == null) {
      this.columns = Collections.emptySet();
    } else {
      this.columns = new TreeSet<>(columns);
    }
  }

  public Schema getSchema() {
    return new SchemaReference(catalogName, schemaName);
  }

  @Override
  public Iterator<ColumnAttributes> iterator() {
    return columns.iterator();
  }
}
