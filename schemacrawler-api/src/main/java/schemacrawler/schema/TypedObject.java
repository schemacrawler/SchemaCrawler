package schemacrawler.schema;


public interface TypedObject<T>
{

  /**
   * Gets the type of the object. Synonym for another getter method.
   * 
   * @return Type of the object
   */
  T getType();

}
