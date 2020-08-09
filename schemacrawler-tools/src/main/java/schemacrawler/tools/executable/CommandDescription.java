/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.executable;


import static sf.util.Utility.isBlank;

import schemacrawler.schema.Property;

public final class CommandDescription
  implements Property
{

  private static final long serialVersionUID = 2444083929278551904L;

  private final String name;
  private final String description;

  public CommandDescription(final String name, final String description)
  {
    if (isBlank(name))
    {
      throw new IllegalArgumentException("Command name not provided");
    }
    this.name = name;

    if (isBlank(description))
    {
      this.description = null;
    }
    else
    {
      this.description = description;
    }
  }

  @Override
  public int compareTo(final Property otherProperty)
  {
    if (otherProperty == null)
    {
      return -1;
    }
    else
    {
      return getName().compareToIgnoreCase(otherProperty.getName());
    }
  }

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
    if (!(obj instanceof CommandDescription))
    {
      return false;
    }
    final CommandDescription other = (CommandDescription) obj;
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

  @Override
  public String getDescription()
  {
    return description == null? "": description;
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public Object getValue()
  {
    return getDescription();
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (name == null? 0: name.hashCode());
    return result;
  }

  @Override
  public String toString()
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(name);
    if (description != null)
    {
      builder
        .append(" - ")
        .append(description);
    }
    return builder.toString();
  }

}
