/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.test.utility;

import java.util.Collection;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.commandline.PluginCommand;
import schemacrawler.tools.executable.commandline.PluginCommandOption;

public class TestCatalogLoader extends BaseCatalogLoader {

  public TestCatalogLoader() {
    super(3);
  }

  @Override
  public Collection<PluginCommandOption> getLoadCommandLineOptions() {
    final PluginCommand pluginCommand =
        PluginCommand.newPluginCommand(this.getClass().getName(), "Catalog load options");
    pluginCommand.addOption(
        "test-load-option",
        Boolean.class,
        "Check that the test option is added to the load command");
    return pluginCommand.getOptions();
  }

  @Override
  public void loadCatalog() throws SchemaCrawlerException {
    // Do nothing
  }
}
