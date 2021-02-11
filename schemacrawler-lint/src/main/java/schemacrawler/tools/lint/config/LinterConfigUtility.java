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
package schemacrawler.tools.lint.config;

import static java.nio.charset.StandardCharsets.UTF_8;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Reader;
import java.util.logging.Level;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.command.lint.options.LintOptions;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.ioresource.InputResourceUtility;

public final class LinterConfigUtility {

  public static final SchemaCrawlerLogger LOGGER =
      SchemaCrawlerLogger.getLogger(LinterConfigUtility.class.getName());

  /**
   * Obtain linter configuration from a system property
   *
   * @return LinterConfigs
   * @throws SchemaCrawlerException
   */
  public static LinterConfigs readLinterConfigs(final LintOptions lintOptions) {
    final LinterConfigs linterConfigs = new LinterConfigs(lintOptions.getConfig());
    final String linterConfigsFile = lintOptions.getLinterConfigs();
    if (!isBlank(linterConfigsFile)) {
      final InputResource inputResource =
          InputResourceUtility.createInputResource(linterConfigsFile);
      try (final Reader reader = inputResource.openNewInputReader(UTF_8)) {
        linterConfigs.parse(reader);
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING, "Could not load linter configs from file, " + linterConfigsFile, e);
      }
    }
    return linterConfigs;
  }

  private LinterConfigUtility() {}
}
