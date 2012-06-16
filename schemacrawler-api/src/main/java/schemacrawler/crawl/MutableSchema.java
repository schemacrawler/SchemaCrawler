/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Procedure;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Synonym;
import schemacrawler.schema.Table;

/**
 * Represents the database schema.
 * 
 * @author Sualeh Fatehi
 */
class MutableSchema
  extends AbstractNamedObject
  implements Schema
{

  private static final long serialVersionUID = 2309958458323938501L;

  private final SchemaReference schemaRef;
  private final ColumnDataTypes columnDataTypes = new ColumnDataTypes();
  private final NamedObjectList<MutableTable> tables = new NamedObjectList<MutableTable>();
  private final NamedObjectList<MutableProcedure> procedures = new NamedObjectList<MutableProcedure>();
  private final NamedObjectList<MutableSynonym> synonyms = new NamedObjectList<MutableSynonym>();

  MutableSchema()
  {
    this(new SchemaReference(null, null));
  }

  MutableSchema(final SchemaReference schemaRef)
  {
    super(schemaRef.getFullName());
    this.schemaRef = schemaRef;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getCatalogName()
   */
  @Override
  public String getCatalogName()
  {
    return schemaRef.getCatalogName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getColumnDataType(java.lang.String)
   */
  @Override
  public MutableColumnDataType getColumnDataType(final String name)
  {
    return columnDataTypes.lookup(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataTypes()
   */
  @Override
  public ColumnDataType[] getColumnDataTypes()
  {
    return columnDataTypes.values()
      .toArray(new ColumnDataType[columnDataTypes.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getFullName()
   */
  @Override
  public String getFullName()
  {
    return schemaRef.getFullName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedure(java.lang.String)
   */
  @Override
  public MutableProcedure getProcedure(final String name)
  {
    return procedures.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedures()
   */
  @Override
  public Procedure[] getProcedures()
  {
    return procedures.values().toArray(new Procedure[procedures.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getSchemaName()
   */
  @Override
  public String getSchemaName()
  {
    return schemaRef.getSchemaName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedure(java.lang.String)
   */
  @Override
  public MutableSynonym getSynonym(final String name)
  {
    return synonyms.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getProcedures()
   */
  @Override
  public Synonym[] getSynonyms()
  {
    return synonyms.values().toArray(new Synonym[synonyms.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getTable(java.lang.String)
   */
  @Override
  public MutableTable getTable(final String name)
  {
    return tables.lookup(this, name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Schema#getTables()
   */
  @Override
  public Table[] getTables()
  {
    return tables.values().toArray(new Table[tables.size()]);
  }

  void addColumnDataType(final MutableColumnDataType columnDataType)
  {
    if (columnDataType != null)
    {
      columnDataTypes.add(columnDataType);
    }
  }

  void addProcedure(final MutableProcedure procedure)
  {
    procedures.add(procedure);
  }

  void addSynonym(final MutableSynonym synonym)
  {
    synonyms.add(synonym);
  }

  void addTable(final MutableTable table)
  {
    tables.add(table);
  }

  MutableColumnDataType lookupColumnDataTypeByType(final int type)
  {
    return columnDataTypes.lookupColumnDataTypeByType(type);
  }

  void removeProcedure(final Procedure procedure)
  {
    procedures.remove(procedure);
  }

  void removeSynonym(final Synonym synonym)
  {
    synonyms.remove(synonym);
  }

  void removeTable(final Table table)
  {
    tables.remove(table);
  }

  void setTablesSortOrder(final NamedObjectSort sort)
  {
    tables.setSortOrder(sort);
  }

}
