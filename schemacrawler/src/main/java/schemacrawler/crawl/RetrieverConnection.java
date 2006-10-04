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

  private final DatabaseMetaData metaData;
  private final String catalog;
  private final String schemaPattern;
  private final String jdbcDriverClassName;
  private final Properties additionalConfiguration;

  RetrieverConnection(final DataSource dataSource,
                      final Properties additionalConfiguration)
    throws SchemaCrawlerException
  {
    if (dataSource == null)
    {
      throw new SchemaCrawlerException("No data source provided");
    }

    if (additionalConfiguration == null)
    {
      this.additionalConfiguration = new Properties();
    }
    else
    {
      this.additionalConfiguration = additionalConfiguration;
    }

    if (dataSource instanceof PropertiesDataSource)
    {
      final PropertiesDataSource propertiesDataSource = (PropertiesDataSource) dataSource;
      jdbcDriverClassName = propertiesDataSource.getJdbcDriverClass();
      catalog = propertiesDataSource.getCatalog();
      schemaPattern = propertiesDataSource.getSourceProperties()
        .getProperty("schemapattern");
    }
    else
    {
      try
      {
        final Connection connection = dataSource.getConnection();
        String driverClassName;
        try
        {
          driverClassName = DriverManager.getDriver(
                                                    connection.getMetaData()
                                                      .getURL()).getClass()
            .getName();
        }
        catch (final SQLException e)
        {
          driverClassName = "";
        }
        jdbcDriverClassName = driverClassName;
        catalog = connection.getCatalog();
        // NOTE: schemaPattern remains null, which is ok for JDBC
        schemaPattern = null;
      }
      catch (final SQLException e)
      {
        throw new SchemaCrawlerException(
                                         "Exception instantiting SchemaCrawler",
                                         e);
      }
    }

    try
    {
      final Connection connection = dataSource.getConnection();
      metaData = connection.getMetaData();
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Error getting database metadata", e);
    }

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
   * Gets the view definitions SQL from the additional configuration.
   * 
   * @return View defnitions SQL.
   */
  String getViewDefinitionsSql()
  {
    return additionalConfiguration.getProperty("view_definitions");
  }

  /**
   * Gets the procedure definitions SQL from the additional
   * configuration.
   * 
   * @return Procedure defnitions SQL.
   */
  String getProcedureDefinitionsSql()
  {
    return additionalConfiguration.getProperty("procedure_definitions");
  }

  void close()
  {
    try
    {
      Connection connection = metaData.getConnection();
      connection.close();
      LOGGER.log(Level.FINE, "Database connection closed - " + connection);
    }
    catch (SQLException e)
    {
      LOGGER.log(Level.WARNING, "Could not close database connection", e);
    }
  }

}
