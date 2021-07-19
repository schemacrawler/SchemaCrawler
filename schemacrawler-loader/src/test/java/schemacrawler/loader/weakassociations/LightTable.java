package schemacrawler.loader.weakassociations;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import schemacrawler.schema.Column;
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
import schemacrawler.schemacrawler.SchemaReference;

public final class LightTable implements Table {

  private static final long serialVersionUID = -309232480533750613L;

  private final Schema schema;
  private final String name;

  public LightTable(final Schema schema, final String name) {
    this.schema = requireNonNull(schema, "No schema provided");
    this.name = requireNotBlank(name, "No table name provided");
  }

  public LightTable(final String name) {
    this(new SchemaReference(), name);
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
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
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
    return null;
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue) throws ClassCastException {
    return null;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return new HashMap<>();
  }

  @Override
  public List<Column> getColumns() {
    return Collections.emptyList();
  }

  @Override
  public String getDefinition() {
    return "";
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
  public Collection<Table> getRelatedTables(final TableRelationshipType tableRelationshipType) {
    return Collections.emptyList();
  }

  @Override
  public String getRemarks() {
    return "";
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
    return Collections.emptyList();
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
    return false;
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
  public boolean hasPrimaryKey() {
    return false;
  }

  @Override
  public boolean hasRemarks() {
    return false;
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
    return Optional.empty();
  }

  @Override
  public <C extends Column> Optional<C> lookupColumn(final String name) {
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
  public void removeAttribute(final String name) {}

  @Override
  public <T> void setAttribute(final String name, final T value) {}

  @Override
  public void setRemarks(final String remarks) {}

  @Override
  public String toString() {
    final StringBuffer buffer = new StringBuffer();
    if (!isBlank(schema.getFullName())) {
      buffer.append(schema).append(".");
    }
    buffer.append(name);
    return buffer.toString();
  }
}
