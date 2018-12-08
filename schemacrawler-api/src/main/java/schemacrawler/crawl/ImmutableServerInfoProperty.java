/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.crawl;


import static sf.util.Utility.isBlank;

import schemacrawler.schema.Property;

final class ImmutableServerInfoProperty
  extends AbstractProperty
{

  private static final long serialVersionUID = -2744384718272515235L;

  private final String description;

  ImmutableServerInfoProperty(final String name,
                              final String value,
                              final String description)
  {
    super(name, value);

    if (isBlank(description))
    {
      this.description = "";
    }
    else
    {
      this.description = description;
    }
  }

  @Override
  public int compareTo(final Property otherDbProperty)
  {
    if (otherDbProperty == null)
    {
      return -1;
    }
    else
    {
      return getName().toLowerCase()
        .compareTo(otherDbProperty.getName().toLowerCase());
    }
  }

  @Override
  public String getDescription()
  {
    return description;
  }

  @Override
  public String toString()
  {
    return getName() + " = " + getValue();
  }

}
