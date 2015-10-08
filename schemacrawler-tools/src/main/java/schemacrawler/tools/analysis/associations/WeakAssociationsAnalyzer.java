/*
 * SchemaCrawler
 * http://www.schemacrawler.com
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
package schemacrawler.tools.analysis.associations;


import static schemacrawler.tools.analysis.associations.WeakAssociationsUtility.addWeakAssociationToTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.requireNonNull;

import schemacrawler.schema.Column;
import schemacrawler.schema.Table;
import schemacrawler.utility.MetaDataUtility;

final class WeakAssociationsAnalyzer
{

  private static final Logger LOGGER = Logger
    .getLogger(WeakAssociationsAnalyzer.class.getName());

  private final List<Table> tables;
  private final Collection<WeakAssociationForeignKey> weakAssociations;

  WeakAssociationsAnalyzer(final List<Table> tables)
  {
    this.tables = requireNonNull(tables, "No tables provided");
    weakAssociations = new TreeSet<>();
  }

  Collection<WeakAssociationForeignKey> analyzeTables()
  {
    if (tables.size() < 2)
    {
      return Collections.emptySet();
    }

    findWeakAssociations(tables);

    return weakAssociations;
  }

  private void addWeakAssociation(final WeakAssociation weakAssociation)
  {
    final String weakFkName = MetaDataUtility
      .constructForeignKeyName(weakAssociation.getPrimaryKeyColumn(),
                               weakAssociation.getForeignKeyColumn());
    final WeakAssociationForeignKey weakFk = new WeakAssociationForeignKey(weakFkName);
    weakFk.add(weakAssociation);

    weakAssociations.add(weakFk);

    addWeakAssociationToTable(weakAssociation.getPrimaryKeyColumn().getParent(),
                              weakFk);
    addWeakAssociationToTable(weakAssociation.getForeignKeyColumn().getParent(),
                              weakFk);
  }

  private void findWeakAssociations(final List<Table> tables)
  {
    LOGGER.log(Level.INFO, "Finding weak associations");
    final ForeignKeys foreignKeys = new ForeignKeys(tables);
    final ColumnMatchKeysMap columnMatchKeysMap = new ColumnMatchKeysMap(tables);
    final TableMatchKeys tableMatchKeys = new TableMatchKeys(tables);

    if (LOGGER.isLoggable(Level.FINER))
    {
      LOGGER.log(Level.FINER,
                 "Column match keys: " + columnMatchKeysMap.toString());
      LOGGER.log(Level.FINER,
                 "Column match keys: " + tableMatchKeys.toString());
    }
    for (final Table table: tables)
    {
      final TableCandidateKeys tableCandidateKeys = new TableCandidateKeys(table);
      LOGGER.log(Level.FINER,
                 "Table candidate keys: " + tableCandidateKeys.toString());
      for (final Column pkColumn: tableCandidateKeys)
      {
        final Set<String> fkColumnMatchKeys = new HashSet<>();
        // Look for all columns matching this table match key
        if (pkColumn.isPartOfPrimaryKey())
        {
          fkColumnMatchKeys.addAll(tableMatchKeys.get(table));
        }
        // Look for all columns matching this column match key
        if (columnMatchKeysMap.containsKey(pkColumn))
        {
          fkColumnMatchKeys.addAll(columnMatchKeysMap.get(pkColumn));
        }

        final Set<Column> fkColumns = new HashSet<>();
        for (final String fkColumnMatchKey: fkColumnMatchKeys)
        {
          if (columnMatchKeysMap.containsKey(fkColumnMatchKey))
          {
            fkColumns.addAll(columnMatchKeysMap.get(fkColumnMatchKey));
          }
        }

        for (final Column fkColumn: fkColumns)
        {
          if (pkColumn.equals(fkColumn))
          {
            continue;
          }

          final WeakAssociation weakAssociation = new WeakAssociation(pkColumn,
                                                                      fkColumn);
          if (weakAssociation.isValid()
              && !foreignKeys.contains(weakAssociation))
          {
            LOGGER.log(Level.FINE,
                       String.format("Found weak association: %s",
                                     weakAssociation));
            addWeakAssociation(weakAssociation);
          }
        }
      }
    }
  }

}
