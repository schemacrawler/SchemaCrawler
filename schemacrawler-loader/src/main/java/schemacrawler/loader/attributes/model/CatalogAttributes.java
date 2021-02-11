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

import schemacrawler.schemacrawler.SchemaCrawlerException;

public class CatalogAttributes extends ObjectAttributes implements Iterable<TableAttributes> {

  private static final long serialVersionUID = 1436642683972751860L;

  private final Set<TableAttributes> tables;

  @ConstructorProperties({"name", "remarks", "attributes", "tables"})
  public CatalogAttributes(
      final String name,
      final List<String> remarks,
      final Map<String, String> attributes,
      final Set<TableAttributes> tables)
      throws SchemaCrawlerException {
    super(name, remarks, attributes);
    if (tables == null) {
      this.tables = Collections.emptySet();
    } else {
      this.tables = new TreeSet<>(tables);
    }
  }

  @Override
  public Iterator<TableAttributes> iterator() {
    return tables.iterator();
  }
}
