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

package schemacrawler.tools.integration;


import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.iosource.InputResourceUtility.createInputResource;
import static sf.util.IOUtility.getFileExtension;
import static sf.util.Utility.isBlank;

import schemacrawler.schemacrawler.Config;
import schemacrawler.tools.iosource.InputResource;

public abstract class BaseLanguage
{

  private final Config config;
  private final String defaultLanguage;
  private final String languageKey;
  private final String resourceKey;

  protected BaseLanguage(final String languageKey,
                         final String resourceKey,
                         final String defaultLanguage)
  {
    this.languageKey = requireNonNull(languageKey, "No language key provided");
    this.resourceKey = requireNonNull(resourceKey, "No resource key provided");
    this.defaultLanguage =
      requireNonNull(defaultLanguage, "No default language provided");
    config = new Config();
  }

  public void addConfig(final Config additionalConfig)
  {
    if (additionalConfig != null && !additionalConfig.isEmpty())
    {
      config.putAll(additionalConfig);
    }
  }

  public final String getLanguage()
  {
    // Check if language is specified
    final String language = config.getStringValue(languageKey, null);
    if (!isBlank(language))
    {
      return language;
    }

    // Use the script file extension if the language is not specified
    final String fileExtension = getFileExtension(getResourceFilename());
    if (!isBlank(fileExtension))
    {
      return fileExtension;
    }

    return defaultLanguage;
  }

  public final InputResource getResource()
  {
    return createInputResource(getResourceFilename());
  }

  public String getResourceFilename()
  {
    return config.getStringValue(resourceKey, null);
  }

}
