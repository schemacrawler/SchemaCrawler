/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schema.Catalog;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.DatabaseUser;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Reducer;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;
import schemacrawler.schemacrawler.SchemaReference;

/**
 * Database and connection information. Created from metadata returned by a JDBC call, and other
 * sources of information.
 */
final class MutableCatalog extends AbstractNamedObjectWithAttributes implements Catalog {

  private static final class FilterBySchema implements Predicate<DatabaseObject> {

    private final Schema schema;

    public FilterBySchema(final Schema schema) {
      this.schema = requireNonNull(schema, "No schema provided");
    }

    @Override
    public boolean test(final DatabaseObject databaseObject) {
      return databaseObject != null && databaseObject.getSchema().equals(schema);
    }
  }

  private static final long serialVersionUID = 4051323422934251828L;

  private final NamedObjectList<MutableColumnDataType> columnDataTypes = new NamedObjectList<>();
  private final MutableDatabaseInfo databaseInfo;
  private final MutableJdbcDriverInfo jdbcDriverInfo;
  private final NamedObjectList<MutableRoutine> routines = new NamedObjectList<>();
  private final NamedObjectList<SchemaReference> schemas = new NamedObjectList<>();
  private final NamedObjectList<MutableSequence> sequences = new NamedObjectList<>();
  private final NamedObjectList<MutableSynonym> synonyms = new NamedObjectList<>();
  private final NamedObjectList<MutableTable> tables = new NamedObjectList<>();
  private final NamedObjectList<ImmutableDatabaseUser> databaseUsers = new NamedObjectList<>();
  private final MutableCrawlInfo crawlInfo;

  MutableCatalog(
      final String name,
      final MutableDatabaseInfo databaseInfo,
      final MutableJdbcDriverInfo jdbcDriverInfo) {
    super(name);

    this.databaseInfo = requireNonNull(databaseInfo, "No database information provided");
    this.jdbcDriverInfo = requireNonNull(jdbcDriverInfo, "No JDBC driver information provided");
    crawlInfo = new MutableCrawlInfo(databaseInfo, jdbcDriverInfo);
  }

  /** {@inheritDoc} */
  @Override
  public Collection<ColumnDataType> getColumnDataTypes() {
    return new ArrayList<>(columnDataTypes.values());
  }

  /** {@inheritDoc} */
  @Override
  public Collection<ColumnDataType> getColumnDataTypes(final Schema schema) {
    final FilterBySchema filter = new FilterBySchema(schema);
    final Collection<ColumnDataType> columnDataTypes = new ArrayList<>();
    for (final ColumnDataType columnDataType : this.columnDataTypes) {
      if (filter.test(columnDataType)) {
        columnDataTypes.add(columnDataType);
      }
    }
    return columnDataTypes;
  }

  @Override
  public CrawlInfo getCrawlInfo() {
    return crawlInfo;
  }

  @Override
  public MutableDatabaseInfo getDatabaseInfo() {
    return databaseInfo;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<DatabaseUser> getDatabaseUsers() {
    return new ArrayList<>(databaseUsers.values());
  }

  /** {@inheritDoc} */
  @Override
  public MutableJdbcDriverInfo getJdbcDriverInfo() {
    return jdbcDriverInfo;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Routine> getRoutines() {
    return new ArrayList<>(routines.values());
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Routine> getRoutines(final Schema schema) {
    return getRoutines(schema, null);
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Routine> getRoutines(final Schema schema, final String routineName) {
    Predicate<DatabaseObject> filter = new FilterBySchema(schema);
    if (!isBlank(routineName)) {
      filter = filter.and(routine -> routine.getName().equals(routineName));
    }
    final Collection<Routine> routines = new ArrayList<>();
    for (final Routine routine : this.routines) {
      if (filter.test(routine)) {
        routines.add(routine);
      }
    }
    return routines;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Schema> getSchemas() {
    return new ArrayList<>(schemas.values());
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Sequence> getSequences() {
    return new ArrayList<>(sequences.values());
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Sequence> getSequences(final Schema schema) {
    final FilterBySchema filter = new FilterBySchema(schema);
    final Collection<Sequence> sequences = new ArrayList<>();
    for (final Sequence sequence : this.sequences) {
      if (filter.test(sequence)) {
        sequences.add(sequence);
      }
    }
    return sequences;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Synonym> getSynonyms() {
    return new ArrayList<>(synonyms.values());
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Synonym> getSynonyms(final Schema schema) {
    final FilterBySchema filter = new FilterBySchema(schema);
    final Collection<Synonym> synonyms = new ArrayList<>();
    for (final Synonym synonym : this.synonyms) {
      if (filter.test(synonym)) {
        synonyms.add(synonym);
      }
    }
    return synonyms;
  }

  /** {@inheritDoc} */
  @Override
  public Collection<ColumnDataType> getSystemColumnDataTypes() {
    return getColumnDataTypes(new SchemaReference());
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Table> getTables() {
    return new ArrayList<>(tables.values());
  }

  /** {@inheritDoc} */
  @Override
  public Collection<Table> getTables(final Schema schema) {
    final FilterBySchema filter = new FilterBySchema(schema);
    final Collection<Table> tables = new ArrayList<>();
    for (final Table table : this.tables) {
      if (filter.test(table)) {
        tables.add(table);
      }
    }
    return tables;
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Column> lookupColumn(
      final Schema schemaRef, final String tableName, final String name) {

    final Optional<MutableTable> tableOptional = lookupTable(schemaRef, tableName);
    if (tableOptional.isPresent()) {
      final Table table = tableOptional.get();
      return table.lookupColumn(name);
    }
    return Optional.empty();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableColumnDataType> lookupColumnDataType(
      final Schema schema, final String name) {
    return columnDataTypes.lookup(schema, name);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<SchemaReference> lookupSchema(final String name) {
    // Schemas need to be looked up by full name, since either the
    // catalog or schema may be null, depending on the database
    if (name == null) {
      return Optional.empty();
    }
    for (final SchemaReference schema : schemas) {
      if (name.equals(schema.getFullName())) {
        return Optional.of(schema);
      }
    }
    return Optional.empty();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableSequence> lookupSequence(final Schema schemaRef, final String name) {
    return sequences.lookup(schemaRef, name);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableSynonym> lookupSynonym(final Schema schemaRef, final String name) {
    return synonyms.lookup(schemaRef, name);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableColumnDataType> lookupSystemColumnDataType(final String name) {
    return lookupColumnDataType(new SchemaReference(), name);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<MutableTable> lookupTable(final Schema schemaRef, final String name) {
    return tables.lookup(schemaRef, name);
  }

  @Override
  public <N extends NamedObject> void reduce(final Class<N> clazz, final Reducer<N> reducer) {
    requireNonNull(reducer, "No reducer provided");
    requireNonNull(clazz, "No lookup class provided");

    if (Schema.class.isAssignableFrom(clazz)) {
      final Reducer<Schema> schemaReducer = (Reducer<Schema>) reducer;
      schemaReducer.reduce(schemas);
    } else if (Table.class.isAssignableFrom(clazz)) {
      // Filter the list of tables based on grep criteria, and
      // parent-child relationships
      final Reducer<Table> tableReducer = (Reducer<Table>) reducer;
      tableReducer.reduce(tables);
    } else if (Routine.class.isAssignableFrom(clazz)) {
      // Filter the list of routines based on grep criteria
      final Reducer<Routine> routineReducer = (Reducer<Routine>) reducer;
      routineReducer.reduce(routines);
    } else if (Synonym.class.isAssignableFrom(clazz)) {
      final Reducer<Synonym> synonymReducer = (Reducer<Synonym>) reducer;
      synonymReducer.reduce(synonyms);
    } else if (Sequence.class.isAssignableFrom(clazz)) {
      final Reducer<Sequence> sequenceReducer = (Reducer<Sequence>) reducer;
      sequenceReducer.reduce(sequences);
    }
  }

  @Override
  public <N extends NamedObject> void undo(final Class<N> clazz, final Reducer<N> reducer) {
    requireNonNull(reducer, "No reducer provided");
    requireNonNull(clazz, "No lookup class provided");

    if (Schema.class.isAssignableFrom(clazz)) {
      final Reducer<Schema> schemaReducer = (Reducer<Schema>) reducer;
      schemaReducer.undo(schemas);
    } else if (Table.class.isAssignableFrom(clazz)) {
      // Filter the list of tables based on grep criteria, and
      // parent-child relationships
      final Reducer<Table> tableReducer = (Reducer<Table>) reducer;
      tableReducer.undo(tables);
    } else if (Routine.class.isAssignableFrom(clazz)) {
      // Filter the list of routines based on grep criteria
      final Reducer<Routine> routineReducer = (Reducer<Routine>) reducer;
      routineReducer.undo(routines);
    } else if (Synonym.class.isAssignableFrom(clazz)) {
      final Reducer<Synonym> synonymReducer = (Reducer<Synonym>) reducer;
      synonymReducer.undo(synonyms);
    } else if (Sequence.class.isAssignableFrom(clazz)) {
      final Reducer<Sequence> sequenceReducer = (Reducer<Sequence>) reducer;
      sequenceReducer.undo(sequences);
    }
  }

  void addColumnDataType(final MutableColumnDataType columnDataType) {
    if (columnDataType != null) {
      columnDataTypes.add(columnDataType);
    }
  }

  void addDatabaseUser(final ImmutableDatabaseUser databaseUser) {
    databaseUsers.add(databaseUser);
  }

  void addRoutine(final MutableRoutine routine) {
    routines.add(routine);
  }

  Schema addSchema(final SchemaReference schema) {
    schemas.add(schema);
    return schema;
  }

  void addSequence(final MutableSequence sequence) {
    sequences.add(sequence);
  }

  void addSynonym(final MutableSynonym synonym) {
    synonyms.add(synonym);
  }

  void addTable(final MutableTable table) {
    tables.add(table);
  }

  NamedObjectList<MutableRoutine> getAllRoutines() {
    return routines;
  }

  NamedObjectList<SchemaReference> getAllSchemas() {
    return schemas;
  }

  NamedObjectList<MutableTable> getAllTables() {
    return tables;
  }

  MutableColumnDataType lookupBaseColumnDataTypeByType(final int baseType) {
    final SchemaReference systemSchema = new SchemaReference();
    MutableColumnDataType columnDataType = null;
    int count = 0;
    for (final MutableColumnDataType currentColumnDataType : columnDataTypes) {
      if (baseType == currentColumnDataType.getJavaSqlType().getVendorTypeNumber()
          && currentColumnDataType.getSchema().equals(systemSchema)) {
        columnDataType = currentColumnDataType;
        count = count + 1;
      }
    }
    if (count == 1) {
      return columnDataType;
    }
    return null;
  }

  Optional<MutableRoutine> lookupRoutine(final NamedObjectKey routineLookupKey) {
    return routines.lookup(routineLookupKey);
  }

  Optional<MutableTable> lookupTable(final NamedObjectKey tableLookupKey) {
    return tables.lookup(tableLookupKey);
  }
}
