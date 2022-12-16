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
package schemacrawler.tools.integration.objectdiffer;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode.State;

public class SchemaCrawlerDifferBuilder {

  final ObjectDifferBuilder objectDifferBuilder;

  public SchemaCrawlerDifferBuilder() {

    objectDifferBuilder = ObjectDifferBuilder.startBuilding();
    objectDifferBuilder.filtering().omitNodesWithState(State.UNTOUCHED);
    objectDifferBuilder.filtering().omitNodesWithState(State.CIRCULAR);
    // All objects
    objectDifferBuilder.inclusion().exclude().propertyName("fullName");
    // Dependent object, to prevent recursive reporting
    objectDifferBuilder.inclusion().exclude().propertyName("parent");
    // Table
    objectDifferBuilder.inclusion().exclude().propertyName("exportedForeignKeys");
    objectDifferBuilder.inclusion().exclude().propertyName("importedForeignKeys");
    // Table constraints
    objectDifferBuilder.inclusion().exclude().propertyName("deferrable");
    objectDifferBuilder.inclusion().exclude().propertyName("initiallyDeferred");
    // Foreign keys
    objectDifferBuilder.inclusion().exclude().propertyName("primaryKeyTable");
    objectDifferBuilder.inclusion().exclude().propertyName("foreignKeyTable");
  }

  public ObjectDiffer build() {
    return objectDifferBuilder.build();
  }
}
