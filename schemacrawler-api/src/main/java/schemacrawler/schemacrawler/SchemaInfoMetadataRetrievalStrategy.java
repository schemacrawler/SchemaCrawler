/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

public enum SchemaInfoMetadataRetrievalStrategy {
  foreignKeysRetrievalStrategy("foreignkeys"),
  functionParametersRetrievalStrategy("functionparameters"),
  functionsRetrievalStrategy("functions"),
  indexesRetrievalStrategy("indexes"),
  primaryKeysRetrievalStrategy("primarykeys"),
  routinesRetrievalStrategy("routines"),
  routineReferencesRetrievalStrategy("routine-references"),
  proceduresRetrievalStrategy("procedures"),
  procedureParametersRetrievalStrategy("procedureparameters"),
  tableColumnPrivilegesRetrievalStrategy("tablecolumnprivileges"),
  tableColumnsRetrievalStrategy("tablecolumns"),
  tablePrivilegesRetrievalStrategy("tableprivileges"),
  tablesRetrievalStrategy("tables"),
  triggersRetrievalStrategy("triggers"),
  tableConstraintsRetrievalStrategy("table-constraints"),
  tableConstraintColumnsRetrievalStrategy("table-constraint-columns"),
  typeInfoRetrievalStrategy("typeinfo"),
  viewInformationRetrievalStrategy("view-information"),
  viewTableUsageRetrievalStrategy("view-table-usage"),
  ;

  private final String key;

  SchemaInfoMetadataRetrievalStrategy(final String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
