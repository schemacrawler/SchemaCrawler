/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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

/**
 * Database and connection information.
 *
 * @author Sualeh Fatehi
 */
public interface Catalog
  extends NamedObject, AttributedObject, DescribedObject
{

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

  SchemaCrawlerInfo getSchemaCrawlerInfo();

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
   * Gets the column data types defined in the schema, by name and sql type number.
   *
   * @param name
   *        Name
   * @param sqlTypeInt
   *        The sql type int from the RDBMS
   * @return Column data types
   */
  Optional<? extends ColumnDataType> lookupColumnDataType(Schema schema,
                                                          int sqlTypeInt,
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
   * Gets a schema by name.
   *
   * @param name
   *        Schema name
   * @return Schema.
   */
  Optional<? extends Schema> lookupSchema(String name);

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
   * Gets the column data types defined by the RDBMS system, by name and sql type number.
   *
   * @param name
   *        Column data type name
   * @param sqlTypeInt
   *        Column data type sql type number
   * @return Column data type
   */
  Optional<? extends ColumnDataType> lookupSystemColumnDataType(int sqlTypeInt, String name);

  /**
   * Gets a table by unqualified name.
   *
   * @param name
   *        Name
   * @return Table.
   */
  Optional<? extends Table> lookupTable(Schema schema, String name);

}
