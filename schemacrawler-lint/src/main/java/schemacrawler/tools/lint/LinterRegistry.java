/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import us.fatehi.utility.string.StringFormat;

/** Linter registry for mapping linters by id. */
public final class LinterRegistry implements Iterable<String> {

  private static final Logger LOGGER = Logger.getLogger(LinterRegistry.class.getName());

  private static final Linter NO_OP_LINTER =
      new BaseLinter() {

        @Override
        public String getLinterId() {
          return "schemacrawler.NO_OP_LINTER";
        }

        @Override
        public String getSummary() {
          return "No-op linter";
        }

        @Override
        protected void lint(final Table table, final Connection connection) {
          // No-op
        }
      };

  private static Map<String, Class<Linter>> loadLinterRegistry() {

    final Map<String, Class<Linter>> linterRegistry = new HashMap<>();
    try {
      final ServiceLoader<Linter> serviceLoader =
          ServiceLoader.load(Linter.class, LinterRegistry.class.getClassLoader());
      for (final Linter linter : serviceLoader) {
        final String linterId = linter.getLinterId();
        final Class<Linter> linterClass = (Class<Linter>) linter.getClass();
        LOGGER.log(
            Level.FINER,
            new StringFormat("Loading linter, %s=%s", linterId, linterClass.getName()));
        linterRegistry.put(linterId, linterClass);
      }
    } catch (final Exception e) {
      throw new InternalRuntimeException("Could not load linter registry", e);
    }

    return linterRegistry;
  }

  private final Map<String, Class<Linter>> linterRegistry;

  public LinterRegistry() {
    linterRegistry = loadLinterRegistry();
  }

  public Set<String> allRegisteredLinters() {
    return new TreeSet<>(linterRegistry.keySet());
  }

  public boolean hasLinter(final String linterId) {
    return linterRegistry.containsKey(linterId);
  }

  @Override
  public Iterator<String> iterator() {
    return allRegisteredLinters().iterator();
  }

  public Linter newLinter(final String linterId) {

    if (hasLinter(linterId)) {
      final Class<Linter> linterClass = linterRegistry.get(linterId);
      try {
        final Linter linter = linterClass.newInstance();
        return linter;
      } catch (final Exception e) {
        LOGGER.log(
            Level.WARNING,
            e,
            new StringFormat("Could not instantiate linter <%s>", linterClass.getName()));
        return NO_OP_LINTER;
      }
    } else {
      LOGGER.log(
          Level.WARNING, new StringFormat("Could not instantiate linter with id <%s>", linterId));
      return NO_OP_LINTER;
    }
  }
}
