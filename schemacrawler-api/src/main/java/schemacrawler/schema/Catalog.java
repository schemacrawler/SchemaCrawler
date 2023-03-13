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

package schemacrawler.schema;

import java.util.Collection;
import java.util.Optional;

/** Database and connection information. */
public interface Catalog extends NamedObject, AttributedObject, DescribedObject, Reducible {
  /**
   * Gets the column data types
   *
   * @return Column data types
   */
  Collection<ColumnDataType> getColumnDataTypes();
  /**
   * Gets the column data types defined in the schema, by name.
   *
   * @return Column data types
   */
  Collection<ColumnDataType> getColumnDataTypes(Schema schema);

  CrawlInfo getCrawlInfo();

  DatabaseInfo getDatabaseInfo();

  /**
   * Gets the database users
   *
   * @return Database users
   */
  Collection<DatabaseUser> getDatabaseUsers();

  JdbcDriverInfo getJdbcDriverInfo();

  /**
   * Gets the routine.
   *
   * @return Routines
   */
  Collection<Routine> getRoutines();

  /**
   * Gets the routine.
   *
   * @return Routines
   */
  Collection<Routine> getRoutines(Schema schema);

  /**
   * Gets a routine by unqualified name.
   *
   * @param schema Schema
   * @param routineName Unqualified routine name
   * @return Routine.
   */
  Collection<Routine> getRoutines(Schema schema, String routineName);

  /**
   * Gets the schemas.
   *
   * @return Schemas
   */
  Collection<Schema> getSchemas();

  /**
   * Gets the sequences.
   *
   * @return Sequences
   */
  Collection<Sequence> getSequences();

  /**
   * Gets the sequences.
   *
   * @return Sequences
   */
  Collection<Sequence> getSequences(Schema schema);

  /**
   * Gets the synonyms.
   *
   * @return Synonyms
   */
  Collection<Synonym> getSynonyms();

  /**
   * Gets the synonyms.
   *
   * @return Synonyms
   */
  Collection<Synonym> getSynonyms(Schema schema);

  /**
   * Gets the column data types defined by the RDBMS system.
   *
   * @return Column data types
   */
  Collection<ColumnDataType> getSystemColumnDataTypes();

  /**
   * Gets the tables.
   *
   * @return Tables
   */
  Collection<Table> getTables();

  /**
   * Gets the tables.
   *
   * @return Tables
   */
  Collection<Table> getTables(Schema schema);

  /**
   * Gets a table column by unqualified name.
   *
   * @param schema Schema
   * @param tableName Unqualified table name
   * @param name Unqualified column name
   * @return Column.
   */
  Optional<Column> lookupColumn(Schema schema, String tableName, String name);

  /**
   * Gets the column data types defined in the schema, by name.
   *
   * @param schema Schema
   * @param dataTypeName Unqualified column data-type name
   * @return Column data type
   */
  <C extends ColumnDataType> Optional<C> lookupColumnDataType(Schema schema, String dataTypeName);

  /**
   * Gets a schema by name.
   *
   * @param name Schema name
   * @return Schema.
   */
  <S extends Schema> Optional<S> lookupSchema(String name);

  /**
   * Gets the sequence by unqualified name.
   *
   * @param schema Schema
   * @param sequenceName Unqualified sequence name
   * @return Sequence.
   */
  <S extends Sequence> Optional<S> lookupSequence(Schema schema, String sequenceName);

  /**
   * Gets the synonym by unqualified name.
   *
   * @param schema Schema
   * @param synonymName Unqualified synonym name
   * @return Synonym.
   */
  <S extends Synonym> Optional<S> lookupSynonym(Schema schema, String synonymName);

  /**
   * Gets the column data types defined by the RDBMS system, by name.
   *
   * @param name Column data type name
   * @return Column data type
   */
  <C extends ColumnDataType> Optional<C> lookupSystemColumnDataType(String name);

  /**
   * Gets a table by unqualified name.
   *
   * @param schema Schema
   * @param tableName Unqualified table name
   * @return Table.
   */
  <T extends Table> Optional<T> lookupTable(Schema schema, String tableName);
}
