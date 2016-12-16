/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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


import java.util.logging.Level;

import schemacrawler.schemacrawler.Options;

public class ApplicationOptions
  implements Options
{

  private static final long serialVersionUID = -2497570007150087268L;

  private Level applicationLogLevel;
  private boolean showHelp;
  private boolean showVersionOnly;

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

  public void setApplicationLogLevel(final Level applicationLogLevel)
  {
    this.applicationLogLevel = applicationLogLevel;
  }

  public void setShowHelp(final boolean showHelp)
  {
    this.showHelp = showHelp;
  }

  public void setShowVersionOnly(final boolean showVersionOnly)
  {
    this.showVersionOnly = showVersionOnly;
  }

}
