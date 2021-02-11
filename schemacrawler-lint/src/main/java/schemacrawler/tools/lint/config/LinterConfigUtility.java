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
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;

import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import schemacrawler.SchemaCrawlerLogger;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerRuntimeException;
import schemacrawler.tools.command.lint.options.LintOptions;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.ioresource.InputResource;
import us.fatehi.utility.ioresource.InputResourceUtility;
import us.fatehi.utility.string.StringFormat;

@UtilityMarker
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
        final List<LinterConfig> linterConfigsList = readLinterConfigs(reader);
        for (final LinterConfig linterConfig : linterConfigsList) {
          linterConfigs.add(linterConfig);
        }
      } catch (final Exception e) {
        throw new SchemaCrawlerRuntimeException(
            "Could not load linter configs from file, " + linterConfigsFile, e);
      }
    }
    return linterConfigs;
  }

  private static List<LinterConfig> readLinterConfigs(final Reader reader)
      throws SchemaCrawlerException {
    requireNonNull(reader, "No input provided");

    try {
      final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
      final CollectionType listType =
          mapper.getTypeFactory().constructCollectionType(List.class, LinterConfig.class);

      final List<LinterConfig> linterConfigs = mapper.readValue(reader, listType);
      Collections.sort(linterConfigs);
      LOGGER.log(Level.CONFIG, new StringFormat("Read <%d> linter configs", linterConfigs.size()));

      return linterConfigs;
    } catch (final Exception e) {
      throw new SchemaCrawlerException("Could not read linter configs", e);
    }
  }

  private LinterConfigUtility() {}
}
