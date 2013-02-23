/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.tools.text.operation;


import static sf.util.Utility.readFully;

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

import schemacrawler.schema.ResultsColumn;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.utility.BinaryData;
import schemacrawler.utility.SchemaCrawlerUtility;

/**
 * Text formatting of data.
 * 
 * @author Sualeh Fatehi
 */
final class DataResultSet
{

  private static final Logger LOGGER = Logger.getLogger(DataResultSet.class
    .getName());

  private final ResultSet rows;
  private final List<ResultsColumn> resultsColumns;
  private final boolean showLobs;

  public DataResultSet(final ResultSet rows, final boolean showLobs)
    throws SchemaCrawlerException
  {
    if (rows == null)
    {
      throw new IllegalArgumentException("Cannot use null results");
    }
    this.rows = rows;

    this.showLobs = showLobs;
    resultsColumns = SchemaCrawlerUtility.getResultColumns(rows).getColumns();
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

  public List row()
    throws SQLException
  {
    final int columnCount = resultsColumns.size();
    final List currentRow = new ArrayList(columnCount);
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
    final int javaSqlType = resultsColumns.get(i).getColumnDataType().getType();
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
