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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static java.util.Objects.requireNonNull;
import schemacrawler.inclusionrule.IncludeAll;
import schemacrawler.inclusionrule.InclusionRule;
import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.Linter;
import schemacrawler.tools.options.Config;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderTableWithBadlyNamedColumns extends BaseLinterProvider {

  private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderTableWithBadlyNamedColumns() {
    super(LinterTableWithBadlyNamedColumns.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterTableWithBadlyNamedColumns(getPropertyName(), lintCollector);
  }
}

class LinterTableWithBadlyNamedColumns extends BaseLinter {

  private InclusionRule columnNames;

  LinterTableWithBadlyNamedColumns(
      final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
  }

  @Override
  public String getSummary() {
    return "badly named column";
  }

  @Override
  protected void configure(final Config config) {
    requireNonNull(config, "No configuration provided");

    final Optional<InclusionRule> inclusionRuleLookup =
        config.getOptionalInclusionRule("bad-column-names", "");
    columnNames = inclusionRuleLookup.orElse(new IncludeAll());
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    final List<Column> badlyNamedColumns = findBadlyNamedColumns(getColumns(table));
    for (final Column column : badlyNamedColumns) {
      addTableLint(table, getSummary(), column);
    }
  }

  private List<Column> findBadlyNamedColumns(final List<Column> columns) {
    final List<Column> badlyNamedColumns = new ArrayList<>();
    if (columnNames == null) {
      return badlyNamedColumns;
    }

    for (final Column column : columns) {
      if (columnNames.test(column.getFullName())) {
        badlyNamedColumns.add(column);
      }
    }
    return badlyNamedColumns;
  }
}
