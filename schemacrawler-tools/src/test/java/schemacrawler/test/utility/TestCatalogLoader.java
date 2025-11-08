/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility;

import schemacrawler.tools.catalogloader.BaseCatalogLoader;
import schemacrawler.tools.executable.commandline.PluginCommand;
import us.fatehi.utility.SystemExitException;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.readconfig.ReadConfig;
import us.fatehi.utility.readconfig.SystemPropertiesConfig;

public class TestCatalogLoader extends BaseCatalogLoader {

  private final ReadConfig config;

  public TestCatalogLoader() {
    super(new PropertyName("testloader", "Loader for testing"), 3);
    forceInstantiationFailureIfConfigured();
    config = new SystemPropertiesConfig();
  }

  @Override
  public PluginCommand getCommandLineCommand() {
    final PropertyName catalogLoaderName = getCatalogLoaderName();
    final PluginCommand pluginCommand = PluginCommand.newCatalogLoaderCommand(catalogLoaderName);
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
        config.getStringValue(this.getClass().getName() + ".force-instantiation-failure");
    if (propertyValue != null) {
      throw new RuntimeException("Forced instantiation error");
    }
  }

  private void forceLoadFailureIfConfigured() {
    final String key = this.getClass().getName() + ".force-load-failure";
    final String propertyValue = config.getStringValue(key);
    if (propertyValue != null) {
      throw new SystemExitException(2, key);
    }
  }
}
