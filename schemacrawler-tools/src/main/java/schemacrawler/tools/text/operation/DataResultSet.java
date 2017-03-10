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

package schemacrawler.tools.text.operation;


import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.readFully;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.crawl.SchemaCrawler;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.utility.BinaryData;

/**
 * Text formatting of data.
 *
 * @author Sualeh Fatehi
 */
final class DataResultSet
{

  private static final Logger LOGGER = Logger
    .getLogger(DataResultSet.class.getName());

  private final ResultSet rows;
  private final List<ResultsColumn> resultsColumns;
  private final boolean showLobs;

  public DataResultSet(final ResultSet rows, final boolean showLobs)
    throws SchemaCrawlerException
  {
    this.rows = requireNonNull(rows, "Cannot use null results");
    this.showLobs = showLobs;
    resultsColumns = SchemaCrawler.getResultColumns(rows).getColumns();
  }

  public String[] getColumnNames()
  {
    final int columnCount = resultsColumns.size();
    final String[] columnNames = new String[columnCount];
    for (int i = 0; i < columnCount; i++)
    {
      columnNames[i] = resultsColumns.get(i).getName();
    }
    return columnNames;
  }

  public boolean next()
    throws SQLException
  {
    return rows.next();
  }

  public List<Object> row()
    throws SQLException
  {
    final int columnCount = resultsColumns.size();
    final List<Object> currentRow = new ArrayList<>(columnCount);
    for (int i = 0; i < columnCount; i++)
    {
      currentRow.add(getColumnData(i));
    }

    return currentRow;
  }

  public int width()
  {
    return resultsColumns.size();
  }

  private Object getColumnData(final int i)
    throws SQLException
  {
    final int javaSqlType = resultsColumns.get(i).getColumnDataType()
      .getJavaSqlType().getJavaSqlType();
    Object columnData;
    if (javaSqlType == Types.CLOB)
    {
      final Clob clob = rows.getClob(i + 1);
      if (rows.wasNull() || clob == null)
      {
        columnData = null;
      }
      else
      {
        columnData = readClob(clob);
      }
    }
    else if (javaSqlType == Types.NCLOB)
    {
      final NClob nClob = rows.getNClob(i + 1);
      if (rows.wasNull() || nClob == null)
      {
        columnData = null;
      }
      else
      {
        columnData = readClob(nClob);
      }
    }
    else if (javaSqlType == Types.BLOB)
    {
      final Blob blob = rows.getBlob(i + 1);
      if (rows.wasNull() || blob == null)
      {
        columnData = null;
      }
      else
      {
        columnData = readBlob(blob);
      }
    }
    else if (javaSqlType == Types.LONGVARBINARY)
    {
      final InputStream stream = rows.getBinaryStream(i + 1);
      if (rows.wasNull() || stream == null)
      {
        columnData = null;
      }
      else
      {
        columnData = readStream(stream);
      }
    }
    else if (javaSqlType == Types.LONGNVARCHAR
             || javaSqlType == Types.LONGVARCHAR)
    {
      final InputStream stream = rows.getAsciiStream(i + 1);
      if (rows.wasNull() || stream == null)
      {
        columnData = null;
      }
      else
      {
        columnData = readStream(stream);
      }
    }
    else
    {
      columnData = rows.getObject(i + 1);
      if (rows.wasNull())
      {
        columnData = null;
      }
    }
    return columnData;
  }

  private BinaryData readBlob(final Blob blob)
  {
    if (blob == null)
    {
      return null;
    }
    else if (showLobs)
    {
      InputStream in = null;
      BinaryData lobData;
      try
      {
        try
        {
          in = blob.getBinaryStream();
        }
        catch (final SQLFeatureNotSupportedException e)
        {
          LOGGER.log(Level.FINEST, "Could not read BLOB data", e);
          in = null;
        }

        if (in != null)
        {
          lobData = new BinaryData(readFully(in));
        }
        else
        {
          lobData = new BinaryData();
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read BLOB data", e);
        lobData = new BinaryData();
      }
      return lobData;
    }
    else
    {
      return new BinaryData();
    }
  }

  private BinaryData readClob(final Clob clob)
  {
    if (clob == null)
    {
      return null;
    }
    else if (showLobs)
    {
      Reader rdr = null;
      BinaryData lobData;
      try
      {
        try
        {
          rdr = clob.getCharacterStream();
        }
        catch (final SQLFeatureNotSupportedException e)
        {
          LOGGER.log(Level.FINEST,
                     "Could not read CLOB data, as character stream",
                     e);
          rdr = null;
        }
        if (rdr == null)
        {
          try
          {
            rdr = new InputStreamReader(clob.getAsciiStream());
          }
          catch (final SQLFeatureNotSupportedException e)
          {
            LOGGER.log(Level.FINEST,
                       "Could not read CLOB data, as ASCII stream",
                       e);
            rdr = null;
          }
        }

        if (rdr != null)
        {
          String lobDataString = readFully(rdr);
          if (lobDataString.isEmpty())
          {
            // Attempt yet another read
            final long clobLength = clob.length();
            lobDataString = clob.getSubString(1, (int) clobLength);
          }
          lobData = new BinaryData(lobDataString);
        }
        else
        {
          lobData = new BinaryData();
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read CLOB data", e);
        lobData = new BinaryData();
      }
      return lobData;
    }
    else
    {
      return new BinaryData();
    }
  }

  /**
   * Reads data from an input stream into a string. Default system
   * encoding is assumed.
   *
   * @param columnData
   *        Column data object returned by JDBC
   * @return A string with the contents of the LOB
   */
  private BinaryData readStream(final InputStream stream)
  {
    if (stream == null)
    {
      return null;
    }
    else if (showLobs)
    {
      final BufferedInputStream in = new BufferedInputStream(stream);
      final BinaryData lobData = new BinaryData(readFully(in));
      return lobData;
    }
    else
    {
      return new BinaryData();
    }
  }

}
