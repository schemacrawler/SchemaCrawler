/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2022, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.util.Collections.emptySet;
import static java.util.Collections.unmodifiableSet;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public final class CatalogAttributes extends ObjectAttributes {

  private static final long serialVersionUID = 1436642683972751860L;

  private final Set<TableAttributes> tables;
  private final Set<WeakAssociationAttributes> weakAssociations;
  private final Set<AlternateKeyAttributes> alternateKeys;

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
      final Set<TableAttributes> tables,
      final Set<WeakAssociationAttributes> weakAssociations,
      final Set<AlternateKeyAttributes> alternateKeys) {
    super(name, remarks, attributes);
    if (tables == null) {
      this.tables = Collections.emptySet();
    } else {
      this.tables = new TreeSet<>(tables);
    }
    if (weakAssociations == null) {
      this.weakAssociations = emptySet();
    } else {
      this.weakAssociations = unmodifiableSet(weakAssociations);
    }
    if (alternateKeys == null) {
      this.alternateKeys = emptySet();
    } else {
      this.alternateKeys = unmodifiableSet(alternateKeys);
    }
  }

  public Set<AlternateKeyAttributes> getAlternateKeys() {
    return alternateKeys;
  }

  public Set<TableAttributes> getTables() {
    return tables;
  }

  public Set<WeakAssociationAttributes> getWeakAssociations() {
    return weakAssociations;
  }
}
