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
package schemacrawler.tools.offline.jdbc;


import static java.util.Objects.requireNonNull;

import java.nio.file.Path;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class OfflineConnection
  implements Connection
{

  private final Path offlineDatabasePath;

  public OfflineConnection(final Path offlineDatabasePath)
  {
    this.offlineDatabasePath = requireNonNull(offlineDatabasePath,
                                              "No offline database path provided");
  }

  @Override
  public void abort(final Executor executor)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void clearWarnings()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void close()
    throws SQLException
  {
  }

  @Override
  public void commit()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Array createArrayOf(final String typeName, final Object[] elements)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Blob createBlob()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Clob createClob()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public NClob createNClob()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public SQLXML createSQLXML()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Statement createStatement()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Statement createStatement(final int resultSetType,
                                   final int resultSetConcurrency)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Statement createStatement(final int resultSetType,
                                   final int resultSetConcurrency,
                                   final int resultSetHoldability)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Struct createStruct(final String typeName, final Object[] attributes)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public boolean getAutoCommit()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public String getCatalog()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Properties getClientInfo()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public String getClientInfo(final String name)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public int getHoldability()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public DatabaseMetaData getMetaData()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public int getNetworkTimeout()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  public Path getOfflineDatabasePath()
  {
    return offlineDatabasePath;
  }

  @Override
  public String getSchema()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public int getTransactionIsolation()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Map<String, Class<?>> getTypeMap()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public SQLWarning getWarnings()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public boolean isClosed()
    throws SQLException
  {
    return false;
  }

  @Override
  public boolean isReadOnly()
    throws SQLException
  {
    return false;
  }

  @Override
  public boolean isValid(final int timeout)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public boolean isWrapperFor(final Class<?> iface)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public String nativeSQL(final String sql)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public CallableStatement prepareCall(final String sql)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public CallableStatement prepareCall(final String sql,
                                       final int resultSetType,
                                       final int resultSetConcurrency)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public CallableStatement prepareCall(final String sql,
                                       final int resultSetType,
                                       final int resultSetConcurrency,
                                       final int resultSetHoldability)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public PreparedStatement prepareStatement(final String sql)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public PreparedStatement prepareStatement(final String sql,
                                            final int autoGeneratedKeys)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public PreparedStatement prepareStatement(final String sql,
                                            final int resultSetType,
                                            final int resultSetConcurrency)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public PreparedStatement prepareStatement(final String sql,
                                            final int resultSetType,
                                            final int resultSetConcurrency,
                                            final int resultSetHoldability)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public PreparedStatement prepareStatement(final String sql,
                                            final int[] columnIndexes)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public PreparedStatement prepareStatement(final String sql,
                                            final String[] columnNames)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void releaseSavepoint(final Savepoint savepoint)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void rollback()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void rollback(final Savepoint savepoint)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setAutoCommit(final boolean autoCommit)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setCatalog(final String catalog)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setClientInfo(final Properties properties)
    throws SQLClientInfoException
  {
    throw new SQLClientInfoException("Not supported", "HYC00", null);
  }

  @Override
  public void setClientInfo(final String name, final String value)
    throws SQLClientInfoException
  {
    throw new SQLClientInfoException("Not supported", "HYC00", null);
  }

  @Override
  public void setHoldability(final int holdability)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setNetworkTimeout(final Executor executor, final int milliseconds)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setReadOnly(final boolean readOnly)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Savepoint setSavepoint()
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public Savepoint setSavepoint(final String name)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setSchema(final String schema)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setTransactionIsolation(final int level)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public void setTypeMap(final Map<String, Class<?>> map)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

  @Override
  public <T> T unwrap(final Class<T> iface)
    throws SQLException
  {
    throw new SQLFeatureNotSupportedException("Not supported", "HYC00");
  }

}
