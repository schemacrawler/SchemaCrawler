/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import static us.fatehi.utility.Utility.isBlank;
import static us.fatehi.utility.Utility.requireNotBlank;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a type of table in the database. Examples could be a base table, a view, a global
 * temporary table, and so on. The table type name is case-sensitive, as it is known to the database
 * system. However, string comparisons are case-insensitive.
 */
public final class TableType implements Serializable, Comparable<TableType> {

  @Serial private static final long serialVersionUID = -8172248482959041873L;
  public static final TableType UNKNOWN = new TableType("unknown");
  private final String tableType;

  /** Constructor for table type. Table type is case-sensitive. */
  public TableType(final String tableTypeString) {
    requireNotBlank(tableTypeString, "No table type provided");
    tableType = tableTypeString.trim();
  }

  /** {@inheritDoc} */
  @Override
  public int compareTo(final TableType other) {
    if (other == null) {
      return -1;
    }

    final String thisToString = toString();
    final String otherToString = other.toString();

    if (thisToString.equalsIgnoreCase(otherToString)) {
      return 0;
    }

    // Sort tables first
    final boolean isThisTable = "TABLE".equalsIgnoreCase(thisToString);
    final boolean isOtherTable = "TABLE".equalsIgnoreCase(otherToString);
    if (isThisTable && !isOtherTable) {
      return -1;
    } else if (!isThisTable && isOtherTable) {
      return 1;
    }

    // Sort views second
    final boolean isThisView = "VIEW".equalsIgnoreCase(thisToString);
    final boolean isOtherView = "VIEW".equalsIgnoreCase(otherToString);
    if (isThisView && !isOtherView) {
      return -1;
    } else if (!isThisView && isOtherView) {
      return 1;
    }

    return thisToString.compareTo(otherToString);
  }

  /** {@inheritDoc} */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || !(obj instanceof TableType)) {
      return false;
    }
    final TableType other = (TableType) obj;
    if (tableType == null) {
      return other.tableType == null;
    } else {
      return tableType.equalsIgnoreCase(other.tableType);
    }
  }

  /**
   * The table type, with the case preserved.
   *
   * @return The table type
   */
  public String getTableType() {
    return tableType;
  }

  /** {@inheritDoc} */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    return prime * result + (tableType == null ? 0 : tableType.toLowerCase().hashCode());
  }

  /**
   * Checks if a string is equal to this table type. This is a case-insensitive check.
   *
   * @return True if the string is the same as this table type
   */
  public boolean isEqualTo(final String testTableType) {
    if (isBlank(testTableType)) {
      return false;
    }
    return tableType.equalsIgnoreCase(testTableType.trim());
  }

  /** Checks if the table type is a view of any kind. */
  public boolean isView() {
    return tableType != null
        && (tableType.toUpperCase().contains("VIEW")
            || tableType.toUpperCase().contains("MATERIALIZED"));
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return tableType.toLowerCase();
  }
}
