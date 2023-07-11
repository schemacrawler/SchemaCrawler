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

package schemacrawler.tools.command.chatgpt.functions;

import static java.util.Objects.requireNonNull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Reducer;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;

final class CatalogWrapper implements Catalog {

  private static final long serialVersionUID = 24506587304709669L;

  private final Collection<Table> tables;

  public CatalogWrapper(final Collection<Table> tables) {
    this.tables = requireNonNull(tables, "No tables provided");
  }

  @Override
  public int compareTo(final NamedObject o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T getAttribute(final String name) {
    return null;
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue) throws ClassCastException {
    return defaultValue;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return Collections.emptyMap();
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes() {
    return Collections.emptyList();
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public CrawlInfo getCrawlInfo() {
    return null;
  }

  @Override
  public DatabaseInfo getDatabaseInfo() {
    return null;
  }

  @Override
  public Collection<DatabaseUser> getDatabaseUsers() {
    return Collections.emptyList();
  }

  @Override
  public String getFullName() {
    return "catalog";
  }

  @Override
  public JdbcDriverInfo getJdbcDriverInfo() {
    return null;
  }

  @Override
  public String getName() {
    return "catalog";
  }

  @Override
  public String getRemarks() {
    return "";
  }

  @Override
  public Collection<Routine> getRoutines() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Routine> getRoutines(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public Collection<Routine> getRoutines(final Schema schema, final String routineName) {
    return Collections.emptyList();
  }

  @Override
  public Collection<Schema> getSchemas() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sequence> getSequences() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Sequence> getSequences(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public Collection<Synonym> getSynonyms() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Synonym> getSynonyms(final Schema schema) {
    return Collections.emptyList();
  }

  @Override
  public Collection<ColumnDataType> getSystemColumnDataTypes() {
    return Collections.emptyList();
  }

  @Override
  public Collection<Table> getTables() {
    return tables;
  }

  @Override
  public Collection<Table> getTables(final Schema schema) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean hasAttribute(final String name) {
    return false;
  }

  @Override
  public boolean hasRemarks() {
    return false;
  }

  @Override
  public NamedObjectKey key() {
    return null;
  }

  @Override
  public <T> Optional<T> lookupAttribute(final String name) {
    return Optional.empty();
  }

  @Override
  public Optional<Column> lookupColumn(
      final Schema schema, final String tableName, final String name) {
    return Optional.empty();
  }

  @Override
  public <C extends ColumnDataType> Optional<C> lookupColumnDataType(
      final Schema schema, final String dataTypeName) {
    return Optional.empty();
  }

  @Override
  public <S extends Schema> Optional<S> lookupSchema(final String name) {
    return Optional.empty();
  }

  @Override
  public <S extends Sequence> Optional<S> lookupSequence(
      final Schema schema, final String sequenceName) {
    return Optional.empty();
  }

  @Override
  public <S extends Synonym> Optional<S> lookupSynonym(
      final Schema schema, final String synonymName) {
    return Optional.empty();
  }

  @Override
  public <C extends ColumnDataType> Optional<C> lookupSystemColumnDataType(final String name) {
    return Optional.empty();
  }

  @Override
  public <T extends Table> Optional<T> lookupTable(final Schema schema, final String tableName) {
    // TODO Auto-generated method stub
    return Optional.empty();
  }

  @Override
  public <N extends NamedObject> void reduce(final Class<N> clazz, final Reducer<N> reducer) {}

  @Override
  public void removeAttribute(final String name) {}

  @Override
  public <T> void setAttribute(final String name, final T value) {}

  @Override
  public void setRemarks(final String remarks) {
    throw new UnsupportedOperationException();
  }
}
