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

package schemacrawler.tools.commandline;


import java.util.Objects;
import java.util.logging.Level;

import schemacrawler.schemacrawler.Options;

public class ApplicationOptions
  implements Options
{

  private final Level applicationLogLevel;
  private final boolean showHelp;
  private final boolean showVersionOnly;

  public ApplicationOptions(final Level applicationLogLevel,
                            final boolean showHelp,
                            final boolean showVersionOnly)
  {
    this.applicationLogLevel = Objects
      .requireNonNull(applicationLogLevel, "No application log level provided");
    this.showHelp = showHelp;
    this.showVersionOnly = showVersionOnly;
  }

  public Level getApplicationLogLevel()
  {
    return applicationLogLevel;
  }

  public boolean isShowHelp()
  {
    return showHelp;
  }

  public boolean isShowVersionOnly()
  {
    return showVersionOnly;
  }

}
