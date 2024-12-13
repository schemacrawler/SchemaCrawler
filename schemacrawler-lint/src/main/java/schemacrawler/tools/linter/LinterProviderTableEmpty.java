/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.linter;

import static schemacrawler.schemacrawler.QueryUtility.executeForLong;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.Objects.requireNonNull;
import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.IdentifiersBuilder;
import schemacrawler.schemacrawler.Query;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linter;
import us.fatehi.utility.property.PropertyName;
import us.fatehi.utility.string.StringFormat;

public class LinterProviderTableEmpty extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableEmpty() {
    super(LinterTableEmpty.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableEmpty(getPropertyName(), lintCollector);
  }
}

class LinterTableEmpty extends BaseLinter {

  private static final Logger LOGGER = Logger.getLogger(LinterTableEmpty.class.getName());

  public LinterTableEmpty(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.low);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary() {
    return "empty table";
  }

  @Override
  public boolean usesConnection() {
    return true;
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");
    requireNonNull(connection, "c");

    final Query query = new Query("Count", "SELECT COUNT(*) FROM ${table}");
    try {
      final Identifiers identifiers =
          IdentifiersBuilder.builder().fromConnection(connection).toOptions();
      final long count = executeForLong(query, connection, table, identifiers);
      if (count == 0) {
        addTableLint(table, getSummary());
      }
    } catch (final SQLException e) {
      LOGGER.log(Level.WARNING, e, new StringFormat("Could not get count for table, ", table));
    }
  }
}
