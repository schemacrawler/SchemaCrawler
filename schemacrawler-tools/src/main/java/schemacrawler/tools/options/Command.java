/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.tools.options;


import static sf.util.Utility.isBlank;

import schemacrawler.schemacrawler.Options;

public final class Command
  implements Options
{

  private static final long serialVersionUID = -3450943894546747834L;

  private final String command;

  public Command(final String command)
  {
    if (isBlank(command))
    {
      throw new IllegalArgumentException("No command specified");
    }
    this.command = command;
  }

  @Override
  public String toString()
  {
    return command;
  }

}
