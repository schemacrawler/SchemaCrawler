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

package schemacrawler.tools.text.operation;


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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hsqldb.types.Types;

import schemacrawler.schema.ResultsColumn;
import schemacrawler.schemacrawler.SchemaCrawlerException;
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

  private static final String NULL = "<null>";
  private static final String BINARY = "<binary>";

  private final ResultSet rows;
  private final ResultsColumn[] resultsColumns;
  private final boolean showLobs;

  public DataResultSet(final ResultSet rows, final boolean showLobs)
    throws SchemaCrawlerException
  {
    try
    {
      if (rows == null || rows.isClosed() || rows.isAfterLast())
      {
        throw new SchemaCrawlerException("Result set cannot be used");
      }
    }
    catch (final SQLException e)
    {
      throw new SchemaCrawlerException("Result set cannot be used", e);
    }

    this.rows = rows;
    this.showLobs = showLobs;
    resultsColumns = SchemaCrawlerUtility.getResultColumns(rows).getColumns();
  }

  public String[] getColumnNames()
  {
    final int columnCount = resultsColumns.length;
    final String[] columnNames = new String[columnCount];
    for (int i = 0; i < columnCount; i++)
    {
      columnNames[i] = resultsColumns[i].getName();
    }
    return columnNames;
  }

  public boolean next()
    throws SQLException
  {
    return rows.next();
  }

  public List<String> row()
    throws SQLException
  {
    final int columnCount = resultsColumns.length;
    final List<String> currentRow = new ArrayList<String>(columnCount);
    for (int i = 0; i < columnCount; i++)
    {
      final String columnDataString = convertColumnDataToString(i);
      currentRow.add(columnDataString);
    }

    return currentRow;
  }

  private String convertColumnDataToString(final int i)
    throws SQLException
  {
    final int javaSqlType = resultsColumns[i].getType().getType();
    String columnDataString;
    if (javaSqlType == Types.CLOB)
    {
      final Clob clob = rows.getClob(i + 1);
      if (rows.wasNull() || clob == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readClob(clob);
      }
    }
    else if (javaSqlType == Types.NCLOB)
    {
      final NClob nClob = rows.getNClob(i + 1);
      if (rows.wasNull() || nClob == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readClob(nClob);
      }
    }
    else if (javaSqlType == Types.BLOB)
    {
      final Blob blob = rows.getBlob(i + 1);
      if (rows.wasNull() || blob == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readBlob(blob);
      }
    }
    else if (javaSqlType == Types.LONGVARBINARY)
    {
      final InputStream stream = rows.getBinaryStream(i + 1);
      if (rows.wasNull() || stream == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readStream(stream);
      }
    }
    else if (javaSqlType == Types.LONGNVARCHAR
             || javaSqlType == Types.LONGVARCHAR)
    {
      final InputStream stream = rows.getAsciiStream(i + 1);
      if (rows.wasNull() || stream == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = readStream(stream);
      }
    }
    else
    {
      final Object columnData = rows.getObject(i + 1);
      if (rows.wasNull() || columnData == null)
      {
        columnDataString = NULL;
      }
      else
      {
        columnDataString = columnData.toString();
      }
    }
    return columnDataString;
  }

  private String readBlob(final Blob blob)
  {
    if (blob == null)
    {
      return NULL;
    }
    else if (showLobs)
    {
      InputStream in = null;
      String lobData;
      try
      {
        try
        {
          in = blob.getBinaryStream();
        }
        catch (final SQLFeatureNotSupportedException e)
        {
          in = null;
        }

        if (in != null)
        {
          lobData = sf.util.Utility.readFully(in);
        }
        else
        {
          lobData = BINARY;
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read BLOB data", e);
        lobData = BINARY;
      }
      return lobData;
    }
    else
    {
      return BINARY;
    }
  }

  private String readClob(final Clob clob)
  {
    if (clob == null)
    {
      return NULL;
    }
    else if (showLobs)
    {
      Reader rdr = null;
      String lobData;
      try
      {
        try
        {
          rdr = clob.getCharacterStream();
        }
        catch (final SQLFeatureNotSupportedException e)
        {
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
            rdr = null;
          }
        }

        if (rdr != null)
        {
          lobData = sf.util.Utility.readFully(rdr);
          if (lobData.length() == 0)
          {
            // Attempt yet another read
            final long clobLength = clob.length();
            lobData = clob.getSubString(1, (int) clobLength);
          }
        }
        else
        {
          lobData = BINARY;
        }
      }
      catch (final SQLException e)
      {
        LOGGER.log(Level.WARNING, "Could not read CLOB data", e);
        lobData = BINARY;
      }
      return lobData;
    }
    else
    {
      return BINARY;
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
  private String readStream(final InputStream stream)
  {
    if (stream == null)
    {
      return NULL;
    }
    else if (showLobs)
    {
      final BufferedInputStream in = new BufferedInputStream(stream);
      final String lobData = sf.util.Utility.readFully(in);
      return lobData;
    }
    else
    {
      return BINARY;
    }
  }

  public int width()
  {
    return resultsColumns.length;
  }
}
