/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */

package schemacrawler.schema;


import java.util.Collection;
import java.util.Optional;

/**
 * Database and connection information.
 *
 * @author Sualeh Fatehi
 */
public interface Catalog
  extends NamedObject, AttributedObject, DescribedObject
{

  /**
   * Gets the column data types defined in the schema, by name.
   *
   * @param name
   *        Name
   * @return Column data type, or null if not found
   */
  @Deprecated
  default ColumnDataType getColumnDataType(final Schema schema,
                                           final String name)
  {
    return lookupColumnDataType(schema, name).orElse(null);
  }

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

  CrawlHeaderInfo getCrawlHeaderInfo();

  DatabaseInfo getDatabaseInfo();

  JdbcDriverInfo getJdbcDriverInfo();

  /**
   * Gets a column by unqualified name.
   *
   * @param name
   *        Unqualified name
   * @return Routine, or null if not found.
   */
  @Deprecated
  default Routine getRoutine(final Schema schema, final String name)
  {
    return lookupRoutine(schema, name).orElse(null);
  }

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
   * Gets a schema by name.
   *
   * @param name
   *        Schema name
   * @return Schema.
   */
  Optional<? extends Schema> getSchema(String name);

  SchemaCrawlerInfo getSchemaCrawlerInfo();

  /**
   * Gets the schemas.
   *
   * @return Schemas
   */
  Collection<Schema> getSchemas();

  /**
   * Gets a sequence by unqualified name.
   *
   * @param name
   *        Unqualified name
   * @return Sequence, or null if not found.
   */
  @Deprecated
  default Sequence getSequence(final Schema schema, final String name)
  {
    return lookupSequence(schema, name).orElse(null);
  }

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
   * Gets a synonym by unqualified name.
   *
   * @param name
   *        Unqualified name
   * @return Synonym, or null if not found.
   */
  @Deprecated
  default Synonym getSynonym(final Schema schema, final String name)
  {
    return lookupSynonym(schema, name).orElse(null);
  }

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
   * Gets the column data types defined by the RDBMS system, by name.
   *
   * @param name
   *        Column data type name
   * @return Column data type
   */
  Optional<? extends ColumnDataType> getSystemColumnDataType(String name);

  /**
   * Gets the column data types defined by the RDBMS system.
   *
   * @return Column data types
   */
  Collection<ColumnDataType> getSystemColumnDataTypes();

  /**
   * Gets a table by unqualified name.
   *
   * @param name
   *        Unqualified name
   * @return Table, or null if not found.
   */
  @Deprecated
  default Table getTable(final Schema schema, final String name)
  {
    return lookupTable(schema, name).orElse(null);
  }

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
   * Gets the column data types defined in the schema, by name.
   *
   * @param name
   *        Name
   * @return Column data types
   */
  Optional<? extends ColumnDataType> lookupColumnDataType(Schema schema,
                                                          String name);

  /**
   * Gets a routine by unqualified name.
   *
   * @param name
   *        Name
   * @return Routine.
   */
  Optional<? extends Routine> lookupRoutine(Schema schema, String name);

  /**
   * Gets the sequence by unqualified name.
   *
   * @param name
   *        Name
   * @return Sequence.
   */
  Optional<? extends Sequence> lookupSequence(Schema schema, String name);

  /**
   * Gets the synonym by unqualified name.
   *
   * @param name
   *        Name
   * @return Synonym.
   */
  Optional<? extends Synonym> lookupSynonym(Schema schema, String name);

  /**
   * Gets a table by unqualified name.
   *
   * @param name
   *        Name
   * @return Table.
   */
  Optional<? extends Table> lookupTable(Schema schema, String name);

}
