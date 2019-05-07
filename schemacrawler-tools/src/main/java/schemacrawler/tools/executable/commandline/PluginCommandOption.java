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
package schemacrawler.tools.executable.commandline;


import static java.util.Objects.requireNonNull;
import static sf.util.Utility.isBlank;

import java.util.Objects;
import java.util.StringJoiner;

class PluginCommandOption
{

  private final String helpText;
  private final String name;
  private final Class<?> valueClass;

  PluginCommandOption(final String name,
                      final String helpText,
                      final Class<?> valueClass)
  {
    this.name = requireNonNull(name, "No option name provided");

    if (isBlank(helpText))
    {
      this.helpText = null;
    }
    else
    {
      this.helpText = helpText;
    }

    if (valueClass == null)
    {
      this.valueClass = String.class;
    }
    else
    {
      this.valueClass = valueClass;
    }
  }

  public Class<?> getValueClass()
  {
    return valueClass;
  }

  public String getName()
  {
    return name;
  }

  public String getHelpText()
  {
    return helpText;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(getName());
  }

  @Override
  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (!(o instanceof PluginCommandOption))
    {
      return false;
    }
    final PluginCommandOption that = (PluginCommandOption) o;
    return Objects.equals(getName(), that.getName());
  }

  @Override
  public String toString()
  {
    return new StringJoiner(", ", "option [", "]").add("name='" + name + "'")
                                                  .add("helpText='" + helpText
                                                       + "'")
                                                  .add(
                                                    "valueClass=" + valueClass)
                                                  .toString();
  }

}
