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
    public void compare(final DiffNode node,
                        final Class<?> type,
                        final Object working,
                        final Object base)
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
  }

  public ObjectDiffer build()
  {
    return objectDifferBuilder.build();
  }

}
