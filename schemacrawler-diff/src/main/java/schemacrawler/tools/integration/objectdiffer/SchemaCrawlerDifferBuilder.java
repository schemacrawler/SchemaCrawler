/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
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
import de.danielbechler.diff.comparison.ComparisonStrategy;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.DiffNode.State;
import schemacrawler.schema.NamedObject;

public class SchemaCrawlerDifferBuilder
{

  private final class NamedObjectComparisonStrategy
    implements ComparisonStrategy
  {
    @Override
    public void compare(final DiffNode node, final Class<?> type,
                        final Object working, final Object base)
    {
      if (NamedObject.class.isAssignableFrom(type))
      {
        if (isEqual((NamedObject) working, (NamedObject) base))
        {
          node.setState(State.UNTOUCHED);
        }
        else
        {
          node.setState(State.CHANGED);
        }
      }
    }

    private boolean isEqual(final NamedObject working, final NamedObject base)
    {
      if (working != null && base != null)
      {
        return working.getName().equals(base.getName());
      }
      return false;
    }
  }

  final ObjectDifferBuilder objectDifferBuilder;

  public SchemaCrawlerDifferBuilder()
  {

    objectDifferBuilder = ObjectDifferBuilder.startBuilding();
    objectDifferBuilder.filtering().omitNodesWithState(State.UNTOUCHED);
    objectDifferBuilder.filtering().omitNodesWithState(State.CIRCULAR);
    objectDifferBuilder.inclusion().exclude().propertyName("fullName");
    objectDifferBuilder.inclusion().exclude().propertyName("parent");
    objectDifferBuilder.inclusion().exclude()
      .propertyName("exportedForeignKeys");
    objectDifferBuilder.inclusion().exclude()
      .propertyName("importedForeignKeys");
    objectDifferBuilder.comparison().ofType(NamedObject.class)
      .toUse(new NamedObjectComparisonStrategy());
    objectDifferBuilder.inclusion().exclude().propertyName("deferrable");
    objectDifferBuilder.inclusion().exclude().propertyName("initiallyDeferred");
  }

  public ObjectDiffer build()
  {
    return objectDifferBuilder.build();
  }

}
