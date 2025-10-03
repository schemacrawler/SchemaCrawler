/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.test.utility.crawl;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;
import static us.fatehi.utility.Utility.trimToEmpty;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import schemacrawler.schema.Column;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.Privilege;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.TableConstraint;
import schemacrawler.schema.TableRelationshipType;
import schemacrawler.schema.TableType;
import schemacrawler.schema.Trigger;
import schemacrawler.schema.WeakAssociation;
import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.SchemaReference;

public final class LightTable implements Table {

  @Serial private static final long serialVersionUID = -309232480533750613L;

  private final Schema schema;
  private final String name;
  private final List<Column> columns;
  private final Map<String, Object> attributes;
  private final Collection<Trigger> triggers;
  private String definition;
  private String remarks;

  public LightTable(final Schema schema, final String name) {
    this.schema = requireNonNull(schema, "No schema provided");
    this.name = requireNotBlank(name, "No table name provided");
    attributes = new HashMap<>();
    columns = new ArrayList<>();
    triggers = new ArrayList<>();
  }

  public LightTable(final String name) {
    this(new SchemaReference(), name);
  }

  public LightColumn addColumn(final String name) {
    final LightColumn column = new LightColumn(this, name);
    columns.add(column);
    return column;
  }

  public void addTrigger(final Trigger trigger) {
    if (trigger != null) {
      triggers.add(trigger);
    }
  }

  @Override
  public int compareTo(final NamedObject o) {
    return name.compareTo(o.getName());
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (getClass() != obj.getClass())) {
      return false;
    }
    final LightTable other = (LightTable) obj;
    return Objects.equals(name, other.name) && Objects.equals(schema, other.schema);
  }

  @Override
  public Collection<PrimaryKey> getAlternateKeys() {
    return Collections.emptyList();
  }

  @Override
  public <T> T getAttribute(final String name) {
    return (T) attributes.get(name);
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue) throws ClassCastException {
    if (hasAttribute(name)) {
      return getAttribute(name);
    }
    return defaultValue;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public List<Column> getColumns() {
    return new ArrayList<>(columns);
  }

  @Override
  public String getDefinition() {
    return trimToEmpty(definition);
  }

  @Override
  public Collection<ForeignKey> getExportedForeignKeys() {
    return Collections.emptyList();
  }

  @Override
  public Collection<ForeignKey> getForeignKeys() {
    return Collections.emptyList();
  }

  @Override
  public String getFullName() {
    return getName();
  }

  @Override
  public Collection<Column> getHiddenColumns() {
    return Collections.emptyList();
  }

  @Override
  public Collection<ForeignKey> getImportedForeignKeys() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Index> getIndexes() {
    return Collections.emptyList();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public PrimaryKey getPrimaryKey() {

    return null;
  }

  @Override
  public Collection<Privilege<Table>> getPrivileges() {
    return Collections.emptyList();
  }

  @Override
  public Collection<DatabaseObject> getUsedByObjects() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType) {
    return Collections.emptyList();
  }

  @Override
  public String getRemarks() {
    return trimToEmpty(remarks);
  }

  @Override
  public Schema getSchema() {
    return schema;
  }

  @Override
  public Collection<TableConstraint> getTableConstraints() {
    return null;
  }

  @Override
  public TableType getTableType() {
    return getType();
  }

  @Override
  public Collection<Trigger> getTriggers() {
    return new ArrayList<>(triggers);
  }

  @Override
  public TableType getType() {
    return TableType.UNKNOWN;
  }

  @Override
  public Collection<WeakAssociation> getWeakAssociations() {
    return null;
  }

  @Override
  public boolean hasAttribute(final String name) {
    return attributes.containsKey(name);
  }

  @Override
  public boolean hasDefinition() {
    return false;
  }

  @Override
  public boolean hasForeignKeys() {
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, schema);
  }

  @Override
  public boolean hasIndexes() {
    return false;
  }

  @Override
  public boolean hasPrimaryKey() {
    return false;
  }

  @Override
  public boolean hasRemarks() {
    return false;
  }

  @Override
  public boolean hasTriggers() {
    return !triggers.isEmpty();
  }

  @Override
  public NamedObjectKey key() {
    return new NamedObjectKey(null, null, name);
  }

  @Override
  public <A extends PrimaryKey> Optional<A> lookupAlternateKey(final String name) {
    return Optional.empty();
  }

  @Override
  public <T> Optional<T> lookupAttribute(final String name) {
    return Optional.of(getAttribute(name));
  }

  @Override
  public <C extends Column> Optional<C> lookupColumn(final String name) {
    for (final Column column : columns) {
      if (column.getName().equals(name)) {
        return (Optional<C>) Optional.of(column);
      }
    }
    return Optional.empty();
  }

  @Override
  public <F extends ForeignKey> Optional<F> lookupForeignKey(final String name) {
    return Optional.empty();
  }

  @Override
  public <I extends Index> Optional<I> lookupIndex(final String name) {
    return Optional.empty();
  }

  @Override
  public <P extends Privilege<Table>> Optional<P> lookupPrivilege(final String name) {
    return Optional.empty();
  }

  @Override
  public <C extends TableConstraint> Optional<C> lookupTableConstraint(final String name) {
    return Optional.empty();
  }

  @Override
  public <T extends Trigger> Optional<T> lookupTrigger(final String name) {
    return Optional.empty();
  }

  @Override
  public void removeAttribute(final String name) {
    attributes.remove(name);
  }

  @Override
  public <T> void setAttribute(final String name, final T value) {
    attributes.put(name, value);
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  @Override
  public void setRemarks(final String remarks) {
    this.remarks = remarks;
  }

  @Override
  public String toString() {
    final StringBuffer buffer = new StringBuffer();
    if (!isBlank(schema.getFullName())) {
      buffer.append(schema).append(".");
    }
    buffer.append(name);
    return buffer.toString();
  }

  @Override
  public void withQuoting(final Identifiers identifiers) {
    // No-op
  }
}
