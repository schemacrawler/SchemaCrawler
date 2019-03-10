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

package schemacrawler.tools.integration.mustache;


import static schemacrawler.tools.iosource.InputResourceUtility.createInputResource;

import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.iosource.InputResource;
import sf.util.SchemaCrawlerLogger;
import sf.util.StringFormat;

/**
 * Main executor for the Mustache integration.
 *
 * @author Sualeh Fatehi
 */
public final class MustacheRenderer
  extends BaseSchemaCrawlerCommand
{

  private static final SchemaCrawlerLogger LOGGER = SchemaCrawlerLogger
    .getLogger(MustacheRenderer.class.getName());

  static final String COMMAND = "mustache";

  public MustacheRenderer()
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

    final String templateLocation = outputOptions.getOutputFormatValue();
    final InputResource inputResource = createInputResource(templateLocation);

    LOGGER.log(Level.INFO,
               new StringFormat("Rendering template <%s> using Mustache",
                                templateLocation));

    final MustacheFactory mustacheFactory = new DefaultMustacheFactory();
    final Mustache mustache = mustacheFactory
      .compile(inputResource.openNewInputReader(StandardCharsets.UTF_8),
               templateLocation);

    // Create the root hash
    final Map<String, Object> context = new HashMap<>();
    context.put("catalog", catalog);
    context.put("identifiers", identifiers);

    try (final Writer writer = outputOptions.openNewOutputWriter();)
    {
      // Evaluate the template
      mustache.execute(writer, context).flush();
    }

  }

  @Override
  public boolean usesConnection()
  {
    return true;
  }

}
