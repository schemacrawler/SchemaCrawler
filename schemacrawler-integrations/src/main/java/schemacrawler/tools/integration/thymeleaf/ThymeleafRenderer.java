/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package schemacrawler.tools.integration.thymeleaf;


import java.io.Writer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.util.logging.Logger;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import schemacrawler.schema.Catalog;
import schemacrawler.tools.executable.BaseStagedExecutable;
import schemacrawler.tools.iosource.OutputWriter;

/**
 * Main executor for the Thymeleaf integration.
 *
 * @author Sualeh Fatehi
 */
public final class ThymeleafRenderer
  extends BaseStagedExecutable
{

  private static final Logger LOGGER = Logger.getLogger(ThymeleafRenderer.class
    .getName());

  static final String COMMAND = "thymeleaf";

  public ThymeleafRenderer()
  {
    super(COMMAND);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final void executeOn(final Catalog catalog, final Connection connection)
    throws Exception
  {
    final Context ctx = new Context();
    ctx.setVariable("catalog", catalog);

    final TemplateEngine templateEngine = new TemplateEngine();
    final Charset inputCharset = outputOptions.getInputCharset();
    templateEngine.addTemplateResolver(configure(new FileTemplateResolver(),
                                                 inputCharset));
    templateEngine
      .addTemplateResolver(configure(new ClassLoaderTemplateResolver(),
                                     inputCharset));
    templateEngine.addTemplateResolver(configure(new UrlTemplateResolver(),
                                                 inputCharset));

    final String templateLocation = outputOptions.getOutputFormatValue();

    try (final Writer writer = new OutputWriter(outputOptions.obtainOutputResource(),
                                                outputOptions
                                                  .getOutputCharset());)
    {
      templateEngine.process(templateLocation, ctx, writer);
    }
  }

  private ITemplateResolver configure(final TemplateResolver templateResolver,
                                      final Charset inputEncoding)
  {
    templateResolver.setCharacterEncoding(inputEncoding.name());
    templateResolver.setTemplateMode("HTML5");
    return templateResolver;
  }

}
