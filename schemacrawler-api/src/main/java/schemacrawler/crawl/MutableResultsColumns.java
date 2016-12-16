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

package schemacrawler.crawl;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import schemacrawler.schema.ResultsColumn;
import schemacrawler.schema.ResultsColumns;

/**
 * Represents a result set, a result of a query.
 *
 * @author Sualeh Fatehi
 */
class MutableResultsColumns
  extends AbstractNamedObject
  implements ResultsColumns
{

  private static final long serialVersionUID = 5204766782914559188L;

  private final NamedObjectList<MutableResultsColumn> columns = new NamedObjectList<>();

  MutableResultsColumns(final String name)
  {
    super(name);
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ResultsColumns#getColumns()
   */
  @Override
  public List<ResultsColumn> getColumns()
  {
    return new ArrayList<>(columns.values());
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ResultsColumns#getColumnsListAsString()
   */
  @Override
  public String getColumnsListAsString()
  {
    String columnsList = "";
    final List<ResultsColumn> columns = getColumns();
    if (columns != null && columns.size() > 0)
    {
      final StringBuilder buffer = new StringBuilder(1024);
      for (int i = 0; i < columns.size(); i++)
      {
        if (i > 0)
        {
          buffer.append(", ");
        }
        final ResultsColumn column = columns.get(i);
        buffer.append(column.getFullName());
      }
      columnsList = buffer.toString();
    }
    return columnsList;
  }

  @Override
  public Iterator<ResultsColumn> iterator()
  {
    return getColumns().iterator();
  }

  /**
   * {@inheritDoc}
   *
   * @see schemacrawler.schema.ResultsColumns#lookupColumn(java.lang.String)
   */
  @Override
  public Optional<MutableResultsColumn> lookupColumn(final String name)
  {
    return columns.lookup(name);
  }

  void addColumn(final MutableResultsColumn column)
  {
    columns.add(column);
  }

}
