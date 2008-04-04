/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.crawl;


import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;

/**
 * Represents the database schema.
 * 
 * @author Sualeh Fatehi
 */
class MutableSchema
  extends AbstractDatabaseObject
  implements Schema
{

  private static final long serialVersionUID = 3258128063743931187L;

  private DatabaseInfo databaseInfo;
  private JdbcDriverInfo driverInfo;
  private final NamedObjectList<MutableTable> tables = new NamedObjectList<MutableTable>(NamedObjectSort.alphabetical);
  private final NamedObjectList<MutableProcedure> procedures = new NamedObjectList<MutableProcedure>(NamedObjectSort.alphabetical);

  MutableSchema(final String catalogName,
                final String schemaName,
                final String name)
  {
    super(catalogName, schemaName, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getDatabaseInfo()
   */
  public DatabaseInfo getDatabaseInfo()
  {
    return databaseInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getJdbcDriverInfo()
   */
  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return driverInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedure(java.lang.String)
   */
  public Procedure getProcedure(final String name)
  {
    return procedures.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedures()
   */
  public Procedure[] getProcedures()
  {
    return procedures.getAll().toArray(new Procedure[procedures.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getTable(java.lang.String)
   */
  public Table getTable(final String name)
  {
    return tables.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getTables()
   */
  public Table[] getTables()
  {
    return tables.getAll().toArray(new Table[tables.size()]);
  }

  void addProcedure(final MutableProcedure procedure)
  {
    procedures.add(procedure);
  }

  void addTable(final MutableTable table)
  {
    tables.add(table);
  }

  void setDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    this.databaseInfo = databaseInfo;
  }

  void setJdbcDriverInfo(final JdbcDriverInfo driverInfo)
  {
    this.driverInfo = driverInfo;
  }

}
