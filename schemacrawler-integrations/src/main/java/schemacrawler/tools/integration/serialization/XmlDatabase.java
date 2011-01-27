/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
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
package schemacrawler.tools.integration.serialization;


import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import schemacrawler.schema.ColumnDataType;
import schemacrawler.schema.Database;
import schemacrawler.schema.DatabaseInfo;
import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.SchemaCrawlerInfo;
import schemacrawler.schemacrawler.SchemaCrawlerException;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.collections.CollectionConverter;
import com.thoughtworks.xstream.converters.collections.MapConverter;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Decorates a database to allow for serialization to and from XML.
 * 
 * @author sfatehi
 */
public final class XmlDatabase
  implements SerializableDatabase
{

  private static final long serialVersionUID = 5314326260124511414L;

  private static XStream newXStream()
    throws SchemaCrawlerException
  {
    try
    {
      final XStream xStream = new XStream();

      xStream.setMode(XStream.ID_REFERENCES);

      xStream.registerConverter(new CollectionConverter(xStream.getMapper())
      {
        @Override
        public void marshal(final Object source,
                            final HierarchicalStreamWriter writer,
                            final MarshallingContext context)
        {
          Collection collection = (Collection) source;
          if (collection instanceof Set)
          {
            final List list = new ArrayList(collection);
            Collections.sort(list);
            collection = list;
          }
          for (final Object item: collection)
          {
            writeItem(item, context, writer);
          }
        }
      }, 5000);
      xStream.registerConverter(new MapConverter(xStream.getMapper())
      {
        @Override
        public void marshal(final Object source,
                            final HierarchicalStreamWriter writer,
                            final MarshallingContext context)
        {
          final Map map = (Map) source;
          final List entryList = new ArrayList(map.entrySet());
          Collections.sort(entryList, new Comparator()
          {
            public int compare(final Object o1, final Object o2)
            {
              return ((Comparable) ((Map.Entry) o1).getKey())
                .compareTo(((Map.Entry) o2).getKey());
            }
          });

          for (final Object object: entryList)
          {
            final Map.Entry entry = (Map.Entry) object;
            ExtendedHierarchicalStreamWriterHelper.startNode(writer, mapper()
              .serializedClass(Map.Entry.class), Map.Entry.class);

            writeItem(entry.getKey(), context, writer);
            writeItem(entry.getValue(), context, writer);

            writer.endNode();
          }
        }
      }, 5000);

      final String[] xmlElements = new String[] {
          "checkConstraint",
          "column",
          "columnDataType",
          "database",
          "databaseProperty",
          "foreignKey",
          "foreignKeyColumnMap",
          "index",
          "indexColumn",
          "jdbcDriverProperty",
          "primaryKey",
          "privilege",
          "procedure",
          "procedureColumn",
          "resultsColumn",
          "resultsColumns",
          "schema",
          "table",
          "trigger",
          "view",
      };
      for (final String xmlElement: xmlElements)
      {
        xStream.alias(xmlElement, Class.forName("schemacrawler.crawl.Mutable"
                                                + xmlElement.substring(0, 1)
                                                  .toUpperCase()
                                                + xmlElement.substring(1)));
      }
      xStream.alias("grant", Class
        .forName("schemacrawler.crawl.MutablePrivilege$PrivilegeGrant"));
      xStream.alias("schemaReference", Class
        .forName("schemacrawler.crawl.SchemaReference"));

      return xStream;
    }
    catch (final ClassNotFoundException e)
    {
      throw new SchemaCrawlerException("Could not load internal classes", e);
    }
  }

  private final Database database;

  public XmlDatabase(final Database database)
  {
    if (database == null)
    {
      throw new IllegalArgumentException("No database provided");
    }
    this.database = database;
  }

  public XmlDatabase(final Reader reader)
    throws SchemaCrawlerException
  {
    this((Database) newXStream().fromXML(reader));
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  public int compareTo(final NamedObject o)
  {
    return database.compareTo(o);
  }

  @Override
  public boolean equals(final Object obj)
  {
    return database.equals(obj);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getAttribute(java.lang.String)
   */
  public Object getAttribute(final String name)
  {
    return database.getAttribute(name);
  }

  public <T> T getAttribute(final String name, final T defaultValue)
  {
    return database.getAttribute(name, defaultValue);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getAttributes()
   */
  public Map<String, Object> getAttributes()
  {
    return database.getAttributes();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getDatabaseInfo()
   */
  public DatabaseInfo getDatabaseInfo()
  {
    return database.getDatabaseInfo();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getFullName()
   */
  public String getFullName()
  {
    return database.getFullName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getJdbcDriverInfo()
   */
  public JdbcDriverInfo getJdbcDriverInfo()
  {
    return database.getJdbcDriverInfo();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getName()
   */
  public String getName()
  {
    return database.getName();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#getRemarks()
   */
  public String getRemarks()
  {
    return database.getRemarks();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchema(java.lang.String)
   */
  public Schema getSchema(final String schemaName)
  {
    return database.getSchema(schemaName);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchemaCrawlerInfo()
   */
  public SchemaCrawlerInfo getSchemaCrawlerInfo()
  {
    return database.getSchemaCrawlerInfo();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSchemas()
   */
  public Schema[] getSchemas()
  {
    return database.getSchemas();
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataType(java.lang.String)
   */
  public ColumnDataType getSystemColumnDataType(final String name)
  {
    return database.getSystemColumnDataType(name);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.Database#getSystemColumnDataTypes()
   */
  public ColumnDataType[] getSystemColumnDataTypes()
  {
    return database.getSystemColumnDataTypes();
  }

  @Override
  public int hashCode()
  {
    return database.hashCode();
  }

  /**
   * Serializes the database to the writer, as XML.
   * 
   * @param writer
   *        Writer to save to
   * @throws SchemaCrawlerException
   *         On an exception
   */
  public void save(final Writer writer)
    throws SchemaCrawlerException
  {
    if (writer == null)
    {
      throw new SchemaCrawlerException("Writer not provided");
    }
    try
    {
      newXStream().toXML(database, writer);
      writer.flush();
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerException("Could not write XML", e);
    }
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.NamedObject#setAttribute(java.lang.String,
   *      java.lang.Object)
   */
  public void setAttribute(final String name, final Object value)
  {
    database.setAttribute(name, value);
  }

}
