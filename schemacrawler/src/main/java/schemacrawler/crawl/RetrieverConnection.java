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


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

/**
 * A connection for the retriever. Wraps a live database connection.
 * 
 * @author Sualeh Fatehi
 */
final class RetrieverConnection
{

  private final Connection connection;
  private final DatabaseMetaData metaData;
  private final InformationSchemaViews informationSchemaViews;
  private final boolean supportsCatalogs;
  protected final Map<SchemaReference, MutableSchema> schemaRefsCache;

  RetrieverConnection(final Connection connection,
                      final SchemaCrawlerOptions options)
    throws SchemaCrawlerException, SQLException
  {
    SchemaCrawlerOptions schemaCrawlerOptions = options;
    if (schemaCrawlerOptions == null)
    {
      schemaCrawlerOptions = new SchemaCrawlerOptions();
    }
    if (connection == null)
    {
      throw new SchemaCrawlerException("No connection provided");
    }
    if (connection.isClosed())
    {
      throw new SchemaCrawlerException("Connection is closed");
    }
    this.connection = connection;
    metaData = connection.getMetaData();
    supportsCatalogs = metaData.supportsCatalogsInTableDefinitions();
    schemaRefsCache = new HashMap<SchemaReference, MutableSchema>();
    informationSchemaViews = schemaCrawlerOptions.getInformationSchemaViews();
  }

  Connection getConnection()
  {
    return connection;
  }

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   * 
   * @return INFORMATION_SCHEMA views selects
   */
  InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  Map<SchemaReference, MutableSchema> getSchemaNamesMap()
  {
    return schemaRefsCache;
  }

  boolean isSupportsCatalogs()
  {
    return supportsCatalogs;
  }

  void cacheSchema(final SchemaReference schemaName, final MutableSchema schema)
  {
    schemaRefsCache.put(schemaName, schema);
  }

}
