package schemacrawler.schema;


@FunctionalInterface
public interface ContainedObject<P>
{

  /**
   * Gets the parent.
   *
   * @return Parent
   */
    P getParent();

}
