/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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


import schemacrawler.schema.JdbcDriverInfo;
import schemacrawler.schema.JdbcDriverProperty;

/**
 * JDBC driver information. Created from metadata returned by a JDBC
 * call, and other sources of information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableJdbcDriverInfo
  implements JdbcDriverInfo
{

  private static final long serialVersionUID = 8030156654422512161L;

  private static final String NEWLINE = System.getProperty("line.separator");

  private String driverName;
  private String driverClassName;
  private String driverVersion;
  private String connectionUrl;
  private boolean jdbcCompliant;
  private final NamedObjectList<MutableJdbcDriverProperty> jdbcDriverProperties = new NamedObjectList<MutableJdbcDriverProperty>(NamedObjectSort.alphabetical);

  /**
   * Adds a JDBC driver property.
   * 
   * @param jdbcDriverProperty
   *        JDBC driver property
   */
  void addJdbcDriverProperty(final MutableJdbcDriverProperty jdbcDriverProperty)
  {
    jdbcDriverProperties.add(jdbcDriverProperty);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (getClass() != obj.getClass())
    {
      return false;
    }
    final MutableJdbcDriverInfo other = (MutableJdbcDriverInfo) obj;
    if (connectionUrl == null)
    {
      if (other.connectionUrl != null)
      {
        return false;
      }
    }
    else if (!connectionUrl.equals(other.connectionUrl))
    {
      return false;
    }
    if (driverName == null)
    {
      if (other.driverName != null)
      {
        return false;
      }
    }
    else if (!driverName.equals(other.driverName))
    {
      return false;
    }
    if (driverVersion == null)
    {
      if (other.driverVersion != null)
      {
        return false;
      }
    }
    else if (!driverVersion.equals(other.driverVersion))
    {
      return false;
    }
    if (driverClassName == null)
    {
      if (other.driverClassName != null)
      {
        return false;
      }
    }
    else if (!driverClassName.equals(other.driverClassName))
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverInfo#getConnectionUrl()
   */
  public String getConnectionUrl()
  {
    return connectionUrl;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverInfo#getDriverClassName()
   */
  public String getDriverClassName()
  {
    return driverClassName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverInfo#getDriverName()
   */
  public String getDriverName()
  {
    return driverName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverInfo#getDriverProperties()
   */
  public JdbcDriverProperty[] getDriverProperties()
  {
    return jdbcDriverProperties.getAll()
      .toArray(new JdbcDriverProperty[jdbcDriverProperties.size()]);
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverInfo#getDriverVersion()
   */
  public String getDriverVersion()
  {
    return driverVersion;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (connectionUrl == null? 0: connectionUrl.hashCode());
    result = prime * result + (driverName == null? 0: driverName.hashCode());
    result = prime * result
             + (driverVersion == null? 0: driverVersion.hashCode());
    result = prime * result
             + (driverClassName == null? 0: driverClassName.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see schemacrawler.schema.JdbcDriverInfo#isJdbcCompliant()
   */
  public boolean isJdbcCompliant()
  {
    return jdbcCompliant;
  }

  void setConnectionUrl(final String connectionUrl)
  {
    this.connectionUrl = connectionUrl;
  }

  void setDriverName(final String driverName)
  {
    this.driverName = driverName;
  }

  void setDriverVersion(final String driverVersion)
  {
    this.driverVersion = driverVersion;
  }

  void setJdbcCompliant(final boolean jdbcCompliant)
  {
    this.jdbcCompliant = jdbcCompliant;
  }

  void setJdbcDriverClassName(final String jdbcDriverClassName)
  {
    driverClassName = jdbcDriverClassName;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {

    final StringBuffer info = new StringBuffer();

    info.append("-- driver: ").append(getDriverName()).append(" ")
      .append(getDriverVersion()).append(NEWLINE);
    info.append("-- driver class: ").append(getDriverClassName())
      .append(NEWLINE);
    info.append("-- url: ").append(getConnectionUrl()).append(NEWLINE);
    info.append("-- jdbc compliant: ").append(isJdbcCompliant());

    return info.toString();

  }

}
