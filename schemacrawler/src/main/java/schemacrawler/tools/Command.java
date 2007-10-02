/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import java.io.Serializable;

import schemacrawler.tools.schematext.SchemaTextDetailType;

/**
 * A single command from the command line.
 * 
 * @author Sualeh Fatehi
 */
public final class Command
  implements Serializable
{

  private static final long serialVersionUID = -3877543572580583938L;

  private final String name;
  private final boolean isQuery;

  /**
   * Constructor.
   * 
   * @param name
   *        Command name.
   */
  public Command(final String name)
  {
    this.name = name;

    SchemaTextDetailType schemaTextDetailType;
    try
    {
      schemaTextDetailType = SchemaTextDetailType.valueOf(name);
    }
    catch (final IllegalArgumentException e)
    {
      schemaTextDetailType = null;
    }
    this.isQuery = schemaTextDetailType == null;
  }

  /**
   * Is this an query?
   * 
   * @return Tool type.
   */
  public boolean isQuery()
  {
    return isQuery;
  }

  /**
   * Gets the command name.
   * 
   * @return Command name.
   */
  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    return name;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (name == null? 0: name.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof Command))
    {
      return false;
    }
    final Command other = (Command) obj;
    if (name == null)
    {
      if (other.name != null)
      {
        return false;
      }
    }
    else if (!name.equals(other.name))
    {
      return false;
    }
    return true;
  }

}
