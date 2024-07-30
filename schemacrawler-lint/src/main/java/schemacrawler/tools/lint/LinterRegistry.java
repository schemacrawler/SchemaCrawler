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

package schemacrawler.tools.lint;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.registry.BasePluginRegistry;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

/** Linter registry for mapping linters by id. */
public final class LinterRegistry extends BasePluginRegistry {

  private static final Logger LOGGER = Logger.getLogger(LinterRegistry.class.getName());

  private static final Linter NO_OP_LINTER =
      new BaseLinter(new PropertyName("schemacrawler.NO_OP_LINTER", ""), new LintCollector()) {

        @Override
        public String getSummary() {
          return "No-op linter";
        }

        @Override
        protected void lint(final Table table, final Connection connection) {
          // No-op
        }
      };

  private static LinterRegistry linterRegistrySingleton;

  public static LinterRegistry getLinterRegistry() {
    if (linterRegistrySingleton == null) {
      linterRegistrySingleton = new LinterRegistry();
    }
    linterRegistrySingleton.log();
    return linterRegistrySingleton;
  }

  private static Map<String, LinterProvider> loadLinterRegistry() {

    final Map<String, LinterProvider> linterRegistry = new HashMap<>();
    try {
      final ServiceLoader<LinterProvider> serviceLoader =
          ServiceLoader.load(LinterProvider.class, LinterRegistry.class.getClassLoader());
      for (final LinterProvider linterProvider : serviceLoader) {
        final String linterId = linterProvider.getLinterId();
        LOGGER.log(Level.FINER, new StringFormat("Loading linter, %s", linterId));
        linterRegistry.put(linterId, linterProvider);
      }
    } catch (final Exception e) {
      throw new InternalRuntimeException("Could not load linter registry", e);
    }

    return linterRegistry;
  }

  private final Map<String, LinterProvider> linterRegistry;

  private LinterRegistry() {
    linterRegistry = loadLinterRegistry();
  }

  @Override
  public String getName() {
    return "Linters";
  }

  public Set<String> getRegisteredLinters() {
    return new HashSet<>(linterRegistry.keySet());
  }

  @Override
  public Collection<PropertyName> getRegisteredPlugins() {
    final List<PropertyName> linters = new ArrayList<>();
    for (final LinterProvider linterInfo : linterRegistry.values()) {
      linters.add(linterInfo.getPropertyName());
    }
    Collections.sort(linters);
    return linters;
  }

  public boolean hasLinter(final String linterId) {
    return linterRegistry.containsKey(linterId);
  }

  public Linter newLinter(final String linterId, final LintCollector lintCollector) {
    requireNonNull(lintCollector, "No lint collector provided");
    if (!hasLinter(linterId)) {
      LOGGER.log(
          Level.WARNING, new StringFormat("Could not instantiate linter with id <%s>", linterId));
      return NO_OP_LINTER;
    }
    final LinterProvider linterProvider = linterRegistry.get(linterId);
    return linterProvider.newLinter(lintCollector);
  }
}
