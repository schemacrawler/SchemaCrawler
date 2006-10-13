/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.tools;


import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * An enumeration wrapper around index types.
 */
public final class ToolType
  implements Serializable
{

  private static final long serialVersionUID = 4049642278194655797L;

  /**
   * 
   */
  public static final ToolType SCHEMA_TEXT = new ToolType("text");
  /**
   * 
   */
  public static final ToolType OPERATION = new ToolType("operation");
  /**
   * 
   */
  public static final ToolType DATA_TEXT = new ToolType("datatext");

  private static final ToolType[] TOOL_TYPE_ALL =
  { SCHEMA_TEXT, OPERATION, DATA_TEXT, };

  private final transient String toolType;

  private ToolType(final String indexType)
  {
    ordinal = nextOrdinal++;
    toolType = indexType;
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param toolTypeString
   *        Value of tool type
   * @return Enumeration value
   */
  public static ToolType valueOf(final String toolTypeString)
  {
    ToolType toolType = null;
    for (int i = 0; i < TOOL_TYPE_ALL.length; i++)
    {
      if (TOOL_TYPE_ALL[i].toolType.equalsIgnoreCase(toolTypeString))
      {
        toolType = TOOL_TYPE_ALL[i];
        break;
      }
    }
    return toolType;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  public String toString()
  {
    return toolType;
  }

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private final int ordinal;

  private static final ToolType[] VALUES = TOOL_TYPE_ALL;

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }
}
