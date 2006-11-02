/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import dbconnector.datasource.PropertiesDataSource;

/**
 * A connection for the retriever.
 * 
 * @author sfatehi
 */
final class RetrieverConnection
{

  private static final Logger LOGGER = Logger
      .getLogger(RetrieverConnection.class.getName());

  private DatabaseMetaData metaData;
  private String catalog;
  private String schemaPattern;
  private String jdbcDriverClassName;
  private InformationSchemaViews informationSchemaViews;

  RetrieverConnection(final DataSource dataSource,
      final Properties additionalConfiguration)
    throws SchemaCrawlerException
  {
    if (dataSource == null)
    {
      throw new SchemaCrawlerException("No data source provided");
    }
    final Connection connection;
    try
    {
      connection = dataSource.getConnection();
      metaData = connection.getMetaData();
    }
    catch (final SQLException e)
    {
      final String errorMessage = e.getMessage();
      LOGGER.log(Level.WARNING, "Could not obtain database metadata: " + errorMessage);      
      throw new SchemaCrawlerException(errorMessage, e);
    }

    if (dataSource instanceof PropertiesDataSource)
    {
      final PropertiesDataSource propertiesDataSource = (PropertiesDataSource) dataSource;
      catalog = propertiesDataSource.getCatalog();
      schemaPattern = propertiesDataSource.getSourceProperties().getProperty(
          "schemapattern");
      jdbcDriverClassName = propertiesDataSource.getJdbcDriverClass();
    } else
    {
      try
      {
        catalog = connection.getCatalog();
        // NOTE: schemaPattern remains null, which is ok for JDBC
        schemaPattern = null;
        jdbcDriverClassName = DriverManager.getDriver(metaData.getURL())
            .getClass().getName();
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "", e);
      }
    }

    informationSchemaViews = new InformationSchemaViews(additionalConfiguration);
  }

  String getCatalog()
  {
    return catalog;
  }

  DatabaseMetaData getMetaData()
  {
    return metaData;
  }

  String getSchemaPattern()
  {
    return schemaPattern;
  }

  String getJdbcDriverClassName()
  {
    return jdbcDriverClassName;
  }

  /**
   * Gets the INFORMATION_SCHEMA views select SQL statements.
   * 
   * @return INFORMATION_SCHEMA views selects
   */
  public InformationSchemaViews getInformationSchemaViews()
  {
    return informationSchemaViews;
  }

  void close()
  {
    try
    {
      final Connection connection = metaData.getConnection();
      connection.close();
      LOGGER.log(Level.FINE, "Database connection closed - " + connection);
    }
    catch (final SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not close database connection", e);
    }
  }

  /**
   * @see java.lang.Object#finalize()
   */
  protected void finalize()
    throws Throwable
  {
    // Release database resources
    if (metaData != null)
    {
      final Connection connection = metaData.getConnection();
      connection.close();
    }
  }

}
