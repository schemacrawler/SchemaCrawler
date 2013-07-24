package schemacrawler.schema;


public interface DefinedObject
{

  /**
   * Gets the definition.
   * 
   * @return Definition
   */
  String getDefinition();

  /**
   * Checks whether there is a definition.
   * 
   * @return True if there is a definition
   */
  boolean hasDefinition();

}
