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

package schemacrawler.tools.operation;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Query;
import schemacrawler.schemacrawler.SchemaInfoLevel;
import schemacrawler.tools.BaseToolOptions;
import schemacrawler.tools.OutputOptions;
import sf.utility.Utility;

/**
 * Operator options.
 * 
 * @author Sualeh Fatehi
 */
public final class OperationOptions
  extends BaseToolOptions
{

  private static final long serialVersionUID = -7977434852526746391L;

  private static final String SHOW_LOBS = "schemacrawler.data.show_lobs";
  private static final String MERGE_ROWS = "schemacrawler.data.merge_rows";

  private boolean mergeRows;
  private boolean showLobs;
  private Operation operation;
  private Query query;

  /**
   * Operator options, defaults.
   */
  public OperationOptions()
  {
    this(null, null, (Operation) null);
  }

  /**
   * Operator options from properties. Constructor.
   * 
   * @param outputOptions
   *        Output options
   * @param operation
   *        Operation
   * @param config
   *        Config
   */
  public OperationOptions(final Config config,
                          final OutputOptions outputOptions,
                          final Operation operation)
  {
    super(outputOptions);
    setFromConfig(config);

    if (operation == null)
    {
      this.operation = Operation.count;
    }
    else
    {
      this.operation = operation;
    }
  }

  /**
   * Operator options from properties. Constructor.
   * 
   * @param outputOptions
   *        Output options
   * @param queryName
   *        Query name
   * @param config
   *        Config
   */
  public OperationOptions(final Config config,
                          final OutputOptions outputOptions,
                          final String queryName)
  {
    super(outputOptions);
    setFromConfig(config);

    if (config != null && !Utility.isBlank(queryName))
    {
      query = new Query(queryName, config.get(queryName));
    }
    else
    {
      query = null;
    }
    operation = null;
  }

  /**
   * Gets the operation.
   * 
   * @return Operation.
   */
  public Operation getOperation()
  {
    return operation;
  }

  /**
   * Get the query.
   * 
   * @return The query
   */
  public Query getQuery()
  {
    if (operation != null)
    {
      return operation.getQuery();
    }
    else
    {
      return query;
    }
  }

  public final SchemaInfoLevel getSchemaInfoLevel()
  {
    final SchemaInfoLevel schemaInfoLevel = new SchemaInfoLevel();
    schemaInfoLevel.setRetrieveDatabaseInfo(true);

    // Retrieve tables only if the query requires it
    final Query query = getQuery();
    if (query == null || query.isQueryOver())
    {
      schemaInfoLevel.setRetrieveColumnDataTypes(true);
      schemaInfoLevel.setRetrieveUserDefinedColumnDataTypes(true);
      schemaInfoLevel.setRetrieveTableColumns(true);
      schemaInfoLevel.setRetrieveTables(true);
    }

    return schemaInfoLevel;
  }

  /**
   * Whether to merge similar rows.
   * 
   * @return Whether to merge similar rows.
   */
  public boolean isMergeRows()
  {
    return mergeRows;
  }

  @Override
  public boolean isPrintVerboseDatabaseInfo()
  {
    return false;
  }

  /**
   * Whether to show LOBs.
   * 
   * @return Whether to show LOBs.
   */
  public boolean isShowLobs()
  {
    return showLobs;
  }

  /**
   * Whether to merge similar rows.
   * 
   * @param mergeRows
   *        Whether to merge similar rows
   */
  public void setMergeRows(final boolean mergeRows)
  {
    this.mergeRows = mergeRows;
  }

  /**
   * Sets the operation.
   * 
   * @param operation
   *        Operation
   */
  public void setOperation(final Operation operation)
  {
    this.operation = operation;
  }

  /**
   * Query.
   * 
   * @param query
   *        Query
   */
  public void setQuery(final Query query)
  {
    if (query == null)
    {
      throw new IllegalArgumentException("Cannot set null query");
    }
    this.query = query;
    operation = null;
  }

  /**
   * Whether to show LOBs.
   * 
   * @param showLobs
   *        Whether to show LOBs
   */
  public void setShowLobs(final boolean showLobs)
  {
    this.showLobs = showLobs;
  }

  private void setFromConfig(final Config config)
  {
    if (config == null)
    {
      mergeRows = false;
      showLobs = false;
    }
    else
    {
      mergeRows = config.getBooleanValue(MERGE_ROWS);
      showLobs = config.getBooleanValue(SHOW_LOBS);
    }
  }

}
