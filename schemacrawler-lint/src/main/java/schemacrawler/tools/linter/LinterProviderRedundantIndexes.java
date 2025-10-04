/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.linter;

import static java.util.Objects.requireNonNull;
import static schemacrawler.tools.lint.LintUtility.listStartsWith;

import java.io.Serial;
import java.sql.Connection;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import schemacrawler.filter.TableTypesFilter;
import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.tools.lint.BaseLinter;
import schemacrawler.tools.lint.BaseLinterProvider;
import schemacrawler.tools.lint.LintCollector;
import schemacrawler.tools.lint.LintSeverity;
import schemacrawler.tools.lint.Linter;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.property.PropertyName;

public class LinterProviderRedundantIndexes extends BaseLinterProvider {

  @Serial private static final long serialVersionUID = -7901644028908017034L;

  public LinterProviderRedundantIndexes() {
    super(LinterRedundantIndexes.class.getName());
  }

  @Override
  public Linter newLinter(final LintCollector lintCollector) {
    return new LinterRedundantIndexes(getPropertyName(), lintCollector);
  }
}

class LinterRedundantIndexes extends BaseLinter {

  LinterRedundantIndexes(final PropertyName propertyName, final LintCollector lintCollector) {
    super(propertyName, lintCollector);
    setSeverity(LintSeverity.high);
    setTableTypesFilter(new TableTypesFilter("TABLE"));
  }

  @Override
  public String getSummary() {
    return "redundant index";
  }

  @Override
  protected void lint(final Table table, final Connection connection) {
    requireNonNull(table, "No table provided");

    final Set<Index> redundantIndexes = findRedundantIndexes(table.getIndexes());
    for (final Index index : redundantIndexes) {
      addTableLint(table, getSummary(), index);
    }
  }

  private Set<Index> findRedundantIndexes(final Collection<Index> indexes) {
    final Set<Index> redundantIndexes = new HashSet<>();

    if (indexes == null || indexes.isEmpty()) {
      return redundantIndexes;
    }

    final Map<Index, List<String>> indexColumns = new HashMap<>(indexes.size());
    for (final Index index : indexes) {
      indexColumns.put(index, MetaDataUtility.columnNames(index));
    }

    for (final Entry<Index, List<String>> indexColumnEntry1 : indexColumns.entrySet()) {
      for (final Entry<Index, List<String>> indexColumnEntry2 : indexColumns.entrySet()) {
        if (!indexColumnEntry1.equals(indexColumnEntry2)
            && listStartsWith(indexColumnEntry1.getValue(), indexColumnEntry2.getValue())) {
          redundantIndexes.add(indexColumnEntry2.getKey());
        }
      }
    }
    return redundantIndexes;
  }
}
