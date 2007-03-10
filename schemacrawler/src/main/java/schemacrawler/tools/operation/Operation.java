/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Database operations.
 */
public final class Operation
  implements Serializable
{

  /** Count operation */
  public static final Operation COUNT = new Operation("COUNT",
                                                      "Row Count",
                                                      "SELECT COUNT(*) FROM ${table}",
                                                      "{0,choice,0#empty|0<{0,number,integer} rows}");

  /** Drop operation */
  public static final Operation DROP = new Operation("DROP",
                                                     "Drop Table",
                                                     "DROP ${tabletype} ${table}",
                                                     "dropped");

  /** Truncate operation */
  public static final Operation TRUNCATE = new Operation("TRUNCATE",
                                                         "Truncate Table",
                                                         "DELETE FROM ${table}",
                                                         "truncated; {0,choice,0#was already empty|0<had {0,number,integer} rows}");

  /** Dump operation */
  public static final Operation DUMP = new Operation("DUMP",
                                                     "Dump",
                                                     "SELECT ${columns} FROM ${table} ORDER BY ${columns}",
                                                     "");

  /** Query-over operation */
  public static final Operation QUERYOVER = new Operation("QUERYOVER",
                                                          "",
                                                          "Query Over Table",
                                                          "{0,choice,0#-|0<{0,number,integer}}");

  private static final long serialVersionUID = -5097434654628745480L;

  private static final Operation[] OPERATION_ALL = new Operation[] {
      COUNT, DROP, TRUNCATE, DUMP, QUERYOVER
  };

  // The 4 declarations below are necessary for serialization
  private static int nextOrdinal;
  private static final Operation[] VALUES = OPERATION_ALL;

  /**
   * Gets the enumeration value for the query over operation.
   * 
   * @return Query over operation
   */
  public static Operation queryOverOperation()
  {
    return QUERYOVER;
  }

  /**
   * Find the enumeration value corresponding to the string.
   * 
   * @param operationString
   *        String value of table type
   * @return Enumeration value
   */
  public static Operation valueOf(final String operationString)
  {

    Operation operation = null;

    for (final Operation element: OPERATION_ALL)
    {
      if (element.toString().equalsIgnoreCase(operationString))
      {
        operation = element;
        break;
      }
    }

    return operation;

  }

  private final transient String operation;

  private final transient String operationDescription;

  private final transient String query;

  private final transient String countMessageFormat;

  private final int ordinal;

  private Operation(final String name,
                    final String description,
                    final String query,
                    final String countMessageFormat)
  {
    ordinal = nextOrdinal++;
    operation = name;
    operationDescription = description;
    this.query = query;
    this.countMessageFormat = countMessageFormat;
  }

  /**
   * Message format for the counts.
   * 
   * @return Message format for the counts
   */
  public String getCountMessageFormat()
  {
    return countMessageFormat;
  }

  /**
   * Operation description.
   * 
   * @return Operation description
   */
  public String getOperationDescription()
  {
    return operationDescription;
  }

  /**
   * Query.
   * 
   * @return Query
   */
  public String getQuery()
  {
    return query;
  }

  /**
   * If this operation is an aggregate operation.
   * 
   * @return If this operation is an aggregate operation
   */
  public boolean isAggregateOperation()
  {
    return this == COUNT;
  }

  /**
   * If this operation is a query-over operation.
   * 
   * @return If this operation is a query-over operation
   */
  public boolean isQueryOver()
  {
    return getOperation().equals(QUERYOVER.getOperation());
  }

  /**
   * If this operation is a select operation.
   * 
   * @return If this operation is a select operation
   */
  public boolean isSelectOperation()
  {
    return this == QUERYOVER || this == DUMP || this == COUNT;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return operation;
  }

  Object readResolve()
    throws ObjectStreamException
  {
    return VALUES[ordinal]; // Canonicalize
  }

  private String getOperation()
  {
    return operation;
  }

}
