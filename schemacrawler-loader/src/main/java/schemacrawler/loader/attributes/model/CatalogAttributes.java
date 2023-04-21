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

import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;
import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Map;

public final class CatalogAttributes extends ObjectAttributes {

  private static final long serialVersionUID = 1436642683972751860L;

  private final List<TableAttributes> tables;
  private final List<WeakAssociationAttributes> weakAssociations;
  private final List<AlternateKeyAttributes> alternateKeys;

  @ConstructorProperties({
    "name",
    "remarks",
    "attributes",
    "tables",
    "weak-associations",
    "alternate-keys"
  })
  public CatalogAttributes(
      final String name,
      final List<String> remarks,
      final Map<String, String> attributes,
      final List<TableAttributes> tables,
      final List<WeakAssociationAttributes> weakAssociations,
      final List<AlternateKeyAttributes> alternateKeys) {
    super(name, remarks, attributes);
    if (tables == null) {
      this.tables = emptyList();
    } else {
      this.tables = unmodifiableList(tables);
    }
    if (weakAssociations == null) {
      this.weakAssociations = emptyList();
    } else {
      this.weakAssociations = unmodifiableList(weakAssociations);
    }
    if (alternateKeys == null) {
      this.alternateKeys = emptyList();
    } else {
      this.alternateKeys = unmodifiableList(alternateKeys);
    }
  }

  public List<AlternateKeyAttributes> getAlternateKeys() {
    return alternateKeys;
  }

  public List<TableAttributes> getTables() {
    return tables;
  }

  public List<WeakAssociationAttributes> getWeakAssociations() {
    return weakAssociations;
  }
}
