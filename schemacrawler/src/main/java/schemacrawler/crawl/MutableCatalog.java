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


import schemacrawler.schema.Catalog;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.Schema;

/**
 * Represents the database.
 * 
 * @author Sualeh Fatehi
 */
class MutableCatalog
  extends AbstractNamedObject
  implements Catalog
{

  private static final long serialVersionUID = 3258128063743931187L;

  private DatabaseInfo databaseInfo;
  private JdbcDriverInfo driverInfo;
  private final NamedObjectList<MutableSchema> schemas = new NamedObjectList<MutableSchema>(NamedObjectSort.alphabetical);

  MutableCatalog(final String name)
  {
    super(name);
  }

  void addSchema(final MutableSchema schema)
  {
    schemas.add(schema);
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
   * @see schemacrawler.schema.Schema#getSchema(java.lang.String)
   */
  public Schema getSchema(final String name)
  {
    return lookupSchema(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getSchemas()
   */
  public Schema[] getSchemas()
  {
    return schemas.getAll().toArray(new Schema[schemas.size()]);
  }

  MutableSchema lookupSchema(final String name)
  {
    return schemas.lookup(name);
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
