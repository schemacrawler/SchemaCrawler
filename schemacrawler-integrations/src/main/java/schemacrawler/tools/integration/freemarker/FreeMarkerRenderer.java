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

package schemacrawler.tools.integration.freemarker;


import java.io.File;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Main executor for the FreeMarker integration.
 *
 * @author Sualeh Fatehi
 */
public final class FreeMarkerRenderer
  extends BaseSchemaCrawlerCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(FreeMarkerRenderer.class.getName());

  static final String COMMAND = "freemarker";

  public FreeMarkerRenderer()
  {
    super(COMMAND);
  }

  @Override
  public void checkAvailibility()
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

    String templateLocation = outputOptions.getOutputFormatValue();
    String templatePath = ".";
    final File templateFilePath = new File(templateLocation);
    if (templateFilePath.exists())
    {
      templatePath = templateFilePath.getAbsoluteFile().getParent();
      templateLocation = templateFilePath.getName();
    }

    System
      .setProperty(freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY,
                   freemarker.log.Logger.LIBRARY_NAME_JUL);

    LOGGER.log(Level.INFO,
               new StringFormat("Rendering using FreeMarker, version %s",
                                Configuration.getVersion()));

    // Create a new instance of the configuration
    final Configuration cfg = new Configuration(Configuration.VERSION_2_3_28);

    final TemplateLoader ctl = new ClassTemplateLoader(FreeMarkerRenderer.class,
                                                       "/");
    final TemplateLoader ftl = new FileTemplateLoader(new File(templatePath));
    final TemplateLoader mtl = new MultiTemplateLoader(new TemplateLoader[] {
                                                                              ctl,
                                                                              ftl });
    cfg.setTemplateLoader(mtl);
    cfg.setEncoding(Locale.getDefault(),
                    outputOptions.getInputCharset().name());
    cfg.setWhitespaceStripping(true);

    LOGGER
      .log(Level.CONFIG,
           new StringFormat("FreeMarker configuration properties <%s>", cfg));

    // Create the root hash
    final Map<String, Object> context = new HashMap<>();
    context.put("catalog", catalog);
    context.put("identifiers", identifiers);

    try (final Writer writer = outputOptions.openNewOutputWriter();)
    {
      // Evaluate the template
      final Template template = cfg.getTemplate(templateLocation);
      template.process(context, writer);
    }
  }

  @Override
  public boolean usesConnection()
  {
    return true;
  }

}
