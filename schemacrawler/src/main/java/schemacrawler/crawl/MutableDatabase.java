/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.crawl;


import schemacrawler.schema.Catalog;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.WeakAssociations;

/**
 * Represents the database.
 * 
 * @author Sualeh Fatehi
 */
class MutableDatabase
  extends AbstractNamedObject
  implements Database
{

  private static final long serialVersionUID = 3258128063743931187L;

  private DatabaseInfo databaseInfo;
  private JdbcDriverInfo driverInfo;
  private final NamedObjectList<MutableCatalog> catalogs = new NamedObjectList<MutableCatalog>(NamedObjectSort.alphabetical);
  private WeakAssociations weakAssociations;

  MutableDatabase(final String name)
  {
    super(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Catalog#getCatalog(java.lang.String)
   */
  public Catalog getCatalog(final String name)
  {
    return lookupCatalog(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Catalog#getCatalogs()
   */
  public Catalog[] getCatalogs()
  {
    return catalogs.getAll().toArray(new Catalog[catalogs.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Catalog#getDatabaseInfo()
   */
  public DatabaseInfo getDatabaseInfo()
  {
    return databaseInfo;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Catalog#getJdbcDriverInfo()
   */
  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return driverInfo;
  }

  public WeakAssociations getWeakAssociations()
  {
    return weakAssociations;
  }

  void addCatalog(final MutableCatalog catalog)
  {
    catalogs.add(catalog);
  }

  MutableCatalog lookupCatalog(final String name)
  {
    return catalogs.lookup(name);
  }

  void setDatabaseInfo(final DatabaseInfo databaseInfo)
  {
    this.databaseInfo = databaseInfo;
  }

  void setJdbcDriverInfo(final JdbcDriverInfo driverInfo)
  {
    this.driverInfo = driverInfo;
  }

  void setWeakAssociations(final WeakAssociations weakAssociations)
  {
    this.weakAssociations = weakAssociations;
  }

}
