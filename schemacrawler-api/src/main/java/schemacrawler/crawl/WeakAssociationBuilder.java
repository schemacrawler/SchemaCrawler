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

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;
import static schemacrawler.crawl.RetrieverUtility.lookupOrCreateColumn;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.PartialDatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableReference;
import schemacrawler.schema.WeakAssociation;
import us.fatehi.utility.string.StringFormat;

public final class WeakAssociationBuilder {

  public static final class WeakAssociationColumn {

    private final Schema schema;
    private final String tableName;
    private final String columnName;

    public WeakAssociationColumn(final Column column) {
      this(
          requireNonNull(column, "No column provided").getSchema(),
          column.getParent().getName(),
          column.getName());
    }

    public WeakAssociationColumn(final Schema schema, final String table, final String column) {
      this.schema = requireNonNull(schema, "No schema provided");
      this.tableName = requireNotBlank(table, "No table name provided");
      this.columnName = requireNotBlank(column, "No column name provided");
    }

    public String getColumnName() {
      return columnName;
    }

    public Schema getSchema() {
      return schema;
    }

    public String getTableName() {
      return tableName;
    }

    @Override
    public String toString() {
      return String.format("weak-association <%s.%s.%s>", schema, tableName, columnName);
    }
  }

  private static final Logger LOGGER = Logger.getLogger(WeakAssociationBuilder.class.getName());

  public static WeakAssociationBuilder builder(final Catalog catalog) {
    return new WeakAssociationBuilder(catalog);
  }

  private final Catalog catalog;
  private final Collection<ColumnReference> columnReferences;

  private WeakAssociationBuilder(final Catalog catalog) {
    this.catalog = requireNonNull(catalog, "No catalog provided");
    columnReferences = new HashSet<>();
  }

  public WeakAssociationBuilder addColumnReference(
      final WeakAssociationColumn referencingColumn, final WeakAssociationColumn referencedColumn) {
    requireNonNull(referencingColumn, "No referencing column provided");
    requireNonNull(referencedColumn, "No referenced column provided");

    final Column fkColumn =
        lookupOrCreateColumn(
            catalog,
            referencingColumn.getSchema(),
            referencingColumn.getTableName(),
            referencingColumn.getColumnName());
    final Column pkColumn =
        lookupOrCreateColumn(
            catalog,
            referencedColumn.getSchema(),
            referencedColumn.getTableName(),
            referencedColumn.getColumnName());

    // Ensure that we have non-null values, since the constructor for WeakAssociationColumn can take
    // any kind of column as arguments
    requireNonNull(fkColumn, "No referencing column provided");
    requireNonNull(pkColumn, "No referenced column provided");

    if (fkColumn.equals(pkColumn)) {
      return this;
    }

    final boolean isFkColumnPartial = fkColumn instanceof PartialDatabaseObject;
    final boolean isPkColumnPartial = pkColumn instanceof PartialDatabaseObject;
    if (isFkColumnPartial && isPkColumnPartial) {
      return this;
    }

    // Start key sequences at index 1
    final int keySequence = columnReferences.size() + 1;
    final ColumnReference columnReference =
        new ImmutableColumnReference(keySequence, fkColumn, pkColumn);
    columnReferences.add(columnReference);

    return this;
  }

  public void build() {
    findOrCreate(null);
  }

  public WeakAssociationBuilder clear() {
    columnReferences.clear();
    LOGGER.log(Level.FINER, new StringFormat("Builder <%s> cleared", hashCode()));
    return this;
  }

  public Optional<TableReference> findOrCreate(final String name) {
    if (columnReferences.isEmpty()) {
      LOGGER.log(Level.CONFIG, "Weak association not built, since there are no column references");
      return Optional.empty();
    }

    final Iterator<ColumnReference> iterator = columnReferences.iterator();

    final ColumnReference someColumnReference = iterator.next();
    final Table referencedTable = someColumnReference.getPrimaryKeyColumn().getParent();
    final Table dependentTable = someColumnReference.getForeignKeyColumn().getParent();

    final String weakAssociationName;
    if (isBlank(name)) {
      weakAssociationName =
          RetrieverUtility.constructForeignKeyName(referencedTable, dependentTable);
    } else {
      weakAssociationName = name;
    }

    final MutableWeakAssociation weakAssociation =
        new MutableWeakAssociation(weakAssociationName, someColumnReference);
    while (iterator.hasNext()) {
      final ColumnReference columnReference = iterator.next();
      // Add a column reference only if they reference the same two tables
      final boolean addedColumnReference = weakAssociation.addColumnReference(columnReference);
      if (!addedColumnReference) {
        LOGGER.log(
            Level.CONFIG,
            new StringFormat(
                "Weak association not built, since column references are not consistent, %s",
                columnReferences));
        return Optional.empty();
      }
    }

    // If there is a matching foreign key, do not create a similar weak association
    final Optional<ForeignKey> optionalMatchingForeignKey =
        lookupMatchingForeignKey(weakAssociation);
    if (optionalMatchingForeignKey.isPresent()) {
      return Optional.of(optionalMatchingForeignKey.get());
    }

    // If there is a matching weak association (checked by the column references), do not create
    // another
    final Optional<WeakAssociation> optionalMatchingWeakAssociation =
        lookupMatchingWeakAssociation(weakAssociation);
    if (optionalMatchingWeakAssociation.isPresent()) {
      return Optional.of(optionalMatchingWeakAssociation.get());
    } else {

      // Add weak association to tables if no matching foreign key is found
      if (referencedTable instanceof MutableTable) {
        ((MutableTable) referencedTable).addWeakAssociation(weakAssociation);
      }
      if (dependentTable instanceof MutableTable) {
        ((MutableTable) dependentTable).addWeakAssociation(weakAssociation);
      }

      return Optional.of(weakAssociation);
    }
  }

  private Optional<ForeignKey> lookupMatchingForeignKey(final WeakAssociation weakAssociation) {
    requireNonNull(weakAssociation, "No weak association provided");

    final Table referencedTable = weakAssociation.getReferencedTable();
    if (!(referencedTable instanceof MutableTable)) {
      return Optional.empty();
    }

    // Search foreign keys by column references
    final Collection<ForeignKey> exportedForeignKeys = referencedTable.getExportedForeignKeys();
    for (final ForeignKey foreignKey : exportedForeignKeys) {
      if (weakAssociation.compareTo(foreignKey) == 0) {
        return Optional.of(foreignKey);
      }
    }

    return Optional.empty();
  }

  private Optional<WeakAssociation> lookupMatchingWeakAssociation(
      final WeakAssociation weakAssociation) {
    requireNonNull(weakAssociation, "No weak association provided");

    final Table referencedTable = weakAssociation.getReferencedTable();
    if (!(referencedTable instanceof MutableTable)) {
      return Optional.empty();
    }

    // Search weak associations by column references
    final Collection<WeakAssociation> weakAssociations = referencedTable.getWeakAssociations();
    for (final WeakAssociation weakAssociationInTable : weakAssociations) {
      if (weakAssociation.compareTo(weakAssociationInTable) == 0) {
        return Optional.of(weakAssociationInTable);
      }
    }

    return Optional.empty();
  }
}
