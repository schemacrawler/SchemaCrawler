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
package schemacrawler.tools.analysis.associations;


import static schemacrawler.tools.analysis.associations.CatalogWithAssociations.addWeakAssociationToTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.ForeignKeyColumnReference;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Table;

final class WeakAssociationsAnalyzer
{

  private static final Logger LOGGER = Logger
    .getLogger(WeakAssociationsAnalyzer.class.getName());

  private final List<Table> tables;
  private final Collection<ColumnReference> weakAssociations;

  WeakAssociationsAnalyzer(final List<Table> tables)
  {
    this.tables = tables;
    weakAssociations = new TreeSet<>();
  }

  Collection<ColumnReference> analyzeTables()
  {
    if (tables == null || tables.size() < 3)
    {
      return Collections.emptySet();
    }

    final TablePrimaries tablePrimaries = new TablePrimaries(tables);
    final Map<String, ForeignKeyColumnReference> fkColumnsMap = mapForeignKeyColumns(tables);
    findWeakAssociations(tables, tablePrimaries, fkColumnsMap);

    return weakAssociations;
  }

  private void addWeakAssociation(final Column fkColumn, final Column pkColumn)
  {
    LOGGER.log(Level.FINE,
               String.format("Found weak association: %s --> %s",
                             fkColumn.getFullName(),
                             pkColumn.getFullName()));
    if (weakAssociations != null)
    {
      final ColumnReference weakAssociation = new WeakAssociation(pkColumn,
                                                                  fkColumn);
      addWeakAssociation(weakAssociation);
    }
  }

  private void addWeakAssociation(final ColumnReference weakAssociation)
  {
    if (weakAssociation != null)
    {
      weakAssociations.add(weakAssociation);

      addWeakAssociationToTable(weakAssociation.getPrimaryKeyColumn()
        .getParent(), weakAssociation);
      addWeakAssociationToTable(weakAssociation.getForeignKeyColumn()
        .getParent(), weakAssociation);
    }
  }

  private void findWeakAssociations(final List<Table> tables,
                                    final TablePrimaries tablePrimaries,
                                    final Map<String, ForeignKeyColumnReference> fkColumnsMap)
  {
    for (final Table table: tables)
    {
      final Map<String, Column> columnNameMatchesMap = mapColumnNameMatches(table);

      for (final Map.Entry<String, Column> columnEntry: columnNameMatchesMap
        .entrySet())
      {
        final String matchColumnName = columnEntry.getKey();
        final List<Table> matchedTables = tablePrimaries.get(matchColumnName);
        if (matchedTables != null)
        {
          for (final Table matchedTable: matchedTables)
          {
            final Column fkColumn = columnEntry.getValue();
            if (matchedTable != null && fkColumn != null
                && !fkColumn.getParent().equals(matchedTable))
            {
              // Check if the table association is already expressed as
              // a foreign key
              final ForeignKeyColumnReference fkColumnReference = fkColumnsMap
                .get(fkColumn.getFullName());
              if (fkColumnReference == null
                  || !fkColumnReference.getPrimaryKeyColumn().getParent()
                    .equals(matchedTable))
              {
                // Ensure that we associate to the primary key
                final Map<String, Column> pkColumnNameMatchesMap = mapColumnNameMatches(matchedTable);
                final Column pkColumn = pkColumnNameMatchesMap.get("id");
                if (pkColumn != null)
                {
                  final ColumnDataType fkColumnType = fkColumn
                    .getColumnDataType();
                  final ColumnDataType pkColumnType = pkColumn
                    .getColumnDataType();
                  if (pkColumnType != null
                      && fkColumnType != null
                      && fkColumnType
                        .getJavaSqlType()
                        .getJavaSqlTypeName()
                        .equals(pkColumnType.getJavaSqlType()
                          .getJavaSqlTypeName()))
                  {
                    addWeakAssociation(fkColumn, pkColumn);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  private Map<String, Column> mapColumnNameMatches(final Table table)
  {
    final Map<String, Column> matchMap = new HashMap<>();

    final PrimaryKey primaryKey = table.getPrimaryKey();
    if (primaryKey != null && primaryKey.getColumns().size() == 1)
    {
      matchMap.put("id", primaryKey.getColumns().get(0));
    }

    for (final Column column: table.getColumns())
    {
      String matchColumnName = column.getName().toLowerCase();
      if (matchColumnName.endsWith("_id"))
      {
        matchColumnName = matchColumnName
          .substring(0, matchColumnName.length() - 3);
      }
      if (matchColumnName.endsWith("id") && !matchColumnName.equals("id"))
      {
        matchColumnName = matchColumnName
          .substring(0, matchColumnName.length() - 2);
      }
      matchMap.put(matchColumnName, column);
    }

    return matchMap;
  }

  private Map<String, ForeignKeyColumnReference> mapForeignKeyColumns(final List<Table> tables)
  {
    final Map<String, ForeignKeyColumnReference> fkColumnsMap = new HashMap<>();
    for (final Table table: tables)
    {
      for (final ForeignKey fk: table.getForeignKeys())
      {
        for (final ForeignKeyColumnReference fkMap: fk.getColumnReferences())
        {
          fkColumnsMap.put(fkMap.getForeignKeyColumn().getFullName(), fkMap);
        }
      }
    }
    return fkColumnsMap;
  }

}
