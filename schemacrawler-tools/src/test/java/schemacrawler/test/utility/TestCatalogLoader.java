/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.property.PropertyName;

public class TestCatalogLoader extends BaseCatalogLoader {

  public TestCatalogLoader() {
    super(new PropertyName("testloader", "Loader for testing"), 3);
    forceInstantiationFailureIfConfigured();
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PropertyName catalogLoaderName = getCatalogLoaderName();
    final PluginCommand pluginCommand =
        PluginCommand.newCatalogLoaderCommand(
            catalogLoaderName.getName(), catalogLoaderName.getDescription());
    pluginCommand.addOption(
        "test-load-option",
        Boolean.class,
        "Check that the test option is added to the load command");
    return pluginCommand;
  }

  @Override
  public void loadCatalog() {
    forceLoadFailureIfConfigured();
  }

  private void forceInstantiationFailureIfConfigured() {
    final String propertyValue =
        System.getProperty(this.getClass().getName() + ".force-instantiation-failure");
    if (propertyValue != null) {
      throw new RuntimeException("Forced instantiation error");
    }
  }

  private void forceLoadFailureIfConfigured() {
    final String propertyValue =
        System.getProperty(this.getClass().getName() + ".force-load-failure");
    if (propertyValue != null) {
      // Hard fail, do not throw, since exceptions are caught
      System.exit(2);
    }
  }
}
