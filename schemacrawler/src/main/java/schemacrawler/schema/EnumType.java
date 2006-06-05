package schemacrawler.schema;


import java.io.Serializable;

/**
 * Base class for enumerations.
 * 
 * @author sfatehi
 */
public interface EnumType
  extends Serializable
{

  /**
   * Id of the enumeration.
   * 
   * @return id
   */
  int getId();

  /**
   * Name of the enumeration.
   * 
   * @return Name
   */
  String getName();

}
