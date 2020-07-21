/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


import static java.sql.Types.BLOB;
import static java.sql.Types.CLOB;
import static java.sql.Types.LONGNVARCHAR;
import static java.sql.Types.LONGVARBINARY;
import static java.sql.Types.LONGVARCHAR;
import static java.sql.Types.NCLOB;
import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.readFully;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import schemacrawler.crawl.ResultsCrawler;
import schemacrawler.schema.ResultsColumn;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.text.utility.BinaryData;
import sf.util.DatabaseUtility;
import sf.util.SchemaCrawlerLogger;

/**
 * Text formatting of data.
 *
 * @author Sualeh Fatehi
 */
final class DataResultSet
{

  private static final SchemaCrawlerLogger LOGGER =
    SchemaCrawlerLogger.getLogger(DataResultSet.class.getName());

  private final ResultSet rows;
  private final List<ResultsColumn> resultsColumns;
  private final boolean showLobs;

  public DataResultSet(final ResultSet rows, final boolean showLobs)
    throws SchemaCrawlerException
  {
    this.rows = requireNonNull(rows, "Cannot use null results");
    this.showLobs = showLobs;
    resultsColumns = new ResultsCrawler(rows)
      .crawl()
      .getColumns();
  }

  public String[] getColumnNames()
  {
    final int columnCount = resultsColumns.size();
    final String[] columnNames = new String[columnCount];
    for (int i = 0; i < columnCount; i++)
    {
      columnNames[i] = resultsColumns
        .get(i)
        .getName();
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
    final int javaSqlType = resultsColumns
      .get(i)
      .getColumnDataType()
      .getJavaSqlType()
      .getVendorTypeNumber();
    Object columnData;

    if (javaSqlType == BLOB || javaSqlType == LONGVARBINARY)
    {
      // Do not read binary data - just determine if it is NULL
      final Object object = rows.getObject(i + 1);
      if (rows.wasNull() || object == null)
      {
        columnData = null;
      }
      else
      {
        columnData = new BinaryData();
      }
    }
    else if (javaSqlType == CLOB)
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
    else if (javaSqlType == NCLOB)
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
    else if (javaSqlType == LONGNVARCHAR || javaSqlType == LONGVARCHAR)
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

  private BinaryData readClob(final Clob clob)
  {
    if (showLobs)
    {
      return new BinaryData(DatabaseUtility.readClob(clob));
    }
    else
    {
      return new BinaryData();
    }
  }

  /**
   * Reads data from an input stream into a string. Default system encoding is
   * assumed.
   *
   * @param stream
   *   Input stream
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
