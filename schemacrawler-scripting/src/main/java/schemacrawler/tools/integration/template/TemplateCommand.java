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

package schemacrawler.tools.integration.template;


import java.util.HashMap;
import java.util.Map;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import sf.util.SchemaCrawlerLogger;

public final class TemplateCommand
  extends BaseSchemaCrawlerCommand
{

  static final String COMMAND = "template";

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger.getLogger(
    TemplateCommand.class.getName());

  private final TemplateLanguage templateLanguage;

  public TemplateCommand()
  {
    super(COMMAND);
    templateLanguage = new TemplateLanguage();
  }

  @Override
  public void checkAvailability()
    throws Exception
  {
    // Nothing to check at this point. The Command should be available
    // after the class is loaded, and imports are resolved.
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void execute()
    throws Exception
  {
    checkCatalog();

    templateLanguage.addConfig(getAdditionalConfiguration());

    final TemplateLanguageType languageType = templateLanguage.getTemplateLanguageType();
    if (languageType == TemplateLanguageType.unknown)
    {
      throw new SchemaCrawlerException("No template language provided");
    }
    final String templateRendererClassName = languageType.getTemplateRendererClassName();
    final Class<TemplateRenderer> templateRendererClass = (Class<TemplateRenderer>) Class
      .forName(templateRendererClassName);
    final TemplateRenderer templateRenderer = templateRendererClass.newInstance();

    final Map<String, Object> context = new HashMap<>();
    context.put("catalog", catalog);
    context.put("identifiers", identifiers);

    templateRenderer.setResourceFilename(templateLanguage.getResourceFilename());
    templateRenderer.setContext(context);
    templateRenderer.setOutputOptions(outputOptions);

    templateRenderer.execute();

  }

  @Override
  public boolean usesConnection()
  {
    return true;
  }

}
