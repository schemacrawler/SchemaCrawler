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
package schemacrawler.schemacrawler;


import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import schemacrawler.schema.Catalog;
import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.CrawlHeaderInfo;
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
  public Object getAttribute(final String name)
  {
    return catalog.getAttribute(name);
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
  public CrawlHeaderInfo getCrawlHeaderInfo()
  {
    return catalog.getCrawlHeaderInfo();
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
  public Optional<? extends Schema> getSchema(final String name)
  {
    return catalog.getSchema(name);
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
  public Optional<? extends ColumnDataType> getSystemColumnDataType(final String name)
  {
    return catalog.getSystemColumnDataType(name);
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
