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
package schemacrawler.schemacrawler;


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.CrawlInfo;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Reducer;
import schemacrawler.schema.Reducible;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schema.Sequence;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;

public abstract class BaseCatalogDecorator
  implements Catalog, Reducible
{

  private static final long serialVersionUID = -3953296149824921463L;

  protected final Catalog catalog;

  public BaseCatalogDecorator(final Catalog catalog)
  {
    this.catalog = requireNonNull(catalog, "No catalog provided");
  }

  @Override
  public int compareTo(final NamedObject o)
  {
    return catalog.compareTo(o);
  }

  @Override
  public <T> T getAttribute(final String name, final T defaultValue)
  {
    return catalog.getAttribute(name, defaultValue);
  }

  @Override
  public Map<String, Object> getAttributes()
  {
    return catalog.getAttributes();
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes()
  {
    return catalog.getColumnDataTypes();
  }

  @Override
  public Collection<ColumnDataType> getColumnDataTypes(final Schema schema)
  {
    return catalog.getColumnDataTypes(schema);
  }

  @Override
  public CrawlInfo getCrawlInfo()
  {
    return catalog.getCrawlInfo();
  }

  @Override
  public DatabaseInfo getDatabaseInfo()
  {
    return catalog.getDatabaseInfo();
  }

  @Override
  public String getFullName()
  {
    return catalog.getFullName();
  }

  @Override
  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return catalog.getJdbcDriverInfo();
  }

  @Override
  public String getLookupKey()
  {
    return getFullName();
  }

  @Override
  public String getName()
  {
    return catalog.getName();
  }

  @Override
  public String getRemarks()
  {
    return catalog.getRemarks();
  }

  @Override
  public Collection<Routine> getRoutines()
  {
    return catalog.getRoutines();
  }

  @Override
  public Collection<Routine> getRoutines(final Schema schema)
  {
    return catalog.getRoutines(schema);
  }

  @Override
  public SchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return catalog.getSchemaCrawlerInfo();
  }

  @Override
  public Collection<Schema> getSchemas()
  {
    return catalog.getSchemas();
  }

  /**
   * @return
   * @see schemacrawler.schema.Catalog#getSequences()
   */
  @Override
  public Collection<Sequence> getSequences()
  {
    return catalog.getSequences();
  }

  /**
   * @param schema
   * @return
   * @see schemacrawler.schema.Catalog#getSequences(schemacrawler.schema.Schema)
   */
  @Override
  public Collection<Sequence> getSequences(final Schema schema)
  {
    return catalog.getSequences(schema);
  }

  @Override
  public Collection<Synonym> getSynonyms()
  {
    return catalog.getSynonyms();
  }

  @Override
  public Collection<Synonym> getSynonyms(final Schema schema)
  {
    return catalog.getSynonyms(schema);
  }

  @Override
  public Collection<ColumnDataType> getSystemColumnDataTypes()
  {
    return catalog.getSystemColumnDataTypes();
  }

  @Override
  public Collection<Table> getTables()
  {
    return catalog.getTables();
  }

  @Override
  public Collection<Table> getTables(final Schema schema)
  {
    return catalog.getTables(schema);
  }

  @Override
  public boolean hasAttribute(final String name)
  {
    return false;
  }

  @Override
  public boolean hasRemarks()
  {
    return catalog.hasRemarks();
  }

  @Override
  public Optional<? extends ColumnDataType> lookupColumnDataType(final Schema schema,
                                                                 final String name)
  {
    return catalog.lookupColumnDataType(schema, name);
  }

  @Override
  public Optional<? extends Routine> lookupRoutine(final Schema schema,
                                                   final String name)
  {
    return catalog.lookupRoutine(schema, name);
  }

  @Override
  public Optional<? extends Schema> lookupSchema(final String name)
  {
    return catalog.lookupSchema(name);
  }

  /**
   * @param schema
   * @param name
   * @return
   * @see schemacrawler.schema.Catalog#lookupSequence(schemacrawler.schema.Schema,
   *      java.lang.String)
   */
  @Override
  public Optional<? extends Sequence> lookupSequence(final Schema schema,
                                                     final String name)
  {
    return catalog.lookupSequence(schema, name);
  }

  @Override
  public Optional<? extends Synonym> lookupSynonym(final Schema schema,
                                                   final String name)
  {
    return catalog.lookupSynonym(schema, name);
  }

  @Override
  public Optional<? extends ColumnDataType> lookupSystemColumnDataType(final String name)
  {
    return catalog.lookupSystemColumnDataType(name);
  }

  @Override
  public Optional<? extends Table> lookupTable(final Schema schema,
                                               final String name)
  {
    return catalog.lookupTable(schema, name);
  }

  @Override
  public <N extends NamedObject> void reduce(final Class<N> clazz,
                                             final Reducer<N> reducer)
  {
    ((Reducible) catalog).reduce(clazz, reducer);
  }

  @Override
  public void removeAttribute(final String name)
  {
    catalog.removeAttribute(name);
  }

  @Override
  public void setAttribute(final String name, final Object value)
  {
    catalog.setAttribute(name, value);
  }

}
