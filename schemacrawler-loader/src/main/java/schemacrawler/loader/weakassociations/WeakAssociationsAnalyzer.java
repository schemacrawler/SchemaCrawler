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
package schemacrawler.loader.weakassociations;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import us.fatehi.utility.string.StringFormat;

public final class WeakAssociationsAnalyzer {

  private static final Logger LOGGER = Logger.getLogger(WeakAssociationsAnalyzer.class.getName());

  private final List<Table> tables;
  private final Predicate<ProposedWeakAssociation> weakAssociationRule;
  private final Collection<ProposedWeakAssociation> weakAssociations;

  public WeakAssociationsAnalyzer(
      final Collection<Table> tables,
      final Predicate<ProposedWeakAssociation> weakAssociationRule) {
    requireNonNull(tables, "No tables provided");
    this.tables = new ArrayList<>(tables);
    Collections.sort(this.tables);

    this.weakAssociationRule = requireNonNull(weakAssociationRule, "No rules provided");

    weakAssociations = new ArrayList<>();
  }

  public Collection<ProposedWeakAssociation> analyzeTables() {
    if (tables.size() < 2) {
      return Collections.emptySet();
    }

    findWeakAssociations(tables);

    return weakAssociations;
  }

  private void findWeakAssociations(final List<Table> tables) {
    LOGGER.log(Level.INFO, "Finding weak associations");
    final ColumnMatchKeysMap columnMatchKeysMap = new ColumnMatchKeysMap(tables);
    final TableMatchKeys tableMatchKeys = new TableMatchKeys(tables);

    if (LOGGER.isLoggable(Level.FINER)) {
      LOGGER.log(Level.FINER, new StringFormat("Column match keys <%s>", columnMatchKeysMap));
      LOGGER.log(Level.FINER, new StringFormat("Table match keys <%s>", tableMatchKeys));
    }
    for (final Table table : tables) {
      final TableCandidateKeys tableCandidateKeys = new TableCandidateKeys(table);
      LOGGER.log(Level.FINER, new StringFormat("Table candidate keys <%s>", tableCandidateKeys));
      for (final Column pkColumn : tableCandidateKeys) {
        final Set<String> fkColumnMatchKeys = new HashSet<>();
        // Look for all columns matching this table match key
        if (pkColumn.isPartOfPrimaryKey()) {
          fkColumnMatchKeys.addAll(tableMatchKeys.get(table));
        }
        // Look for all columns matching this column match key
        if (columnMatchKeysMap.containsKey(pkColumn)) {
          fkColumnMatchKeys.addAll(columnMatchKeysMap.get(pkColumn));
        }

        final Set<Column> fkColumns = new HashSet<>();
        for (final String fkColumnMatchKey : fkColumnMatchKeys) {
          if (columnMatchKeysMap.containsKey(fkColumnMatchKey)) {
            fkColumns.addAll(columnMatchKeysMap.get(fkColumnMatchKey));
          }
        }

        for (final Column fkColumn : fkColumns) {
          final ProposedWeakAssociation proposedWeakAssociation =
              new ProposedWeakAssociation(fkColumn, pkColumn);
          if (proposedWeakAssociation.isValid()
              && weakAssociationRule.test(proposedWeakAssociation)) {
            LOGGER.log(
                Level.FINE,
                new StringFormat("Found weak association <%s>", proposedWeakAssociation));
            weakAssociations.add(proposedWeakAssociation);
          }
        }
      }
    }
  }
}
