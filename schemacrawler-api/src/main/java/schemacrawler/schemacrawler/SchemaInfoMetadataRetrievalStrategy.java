/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
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
  tableAdditionalAttributesRetrievalStrategy("table-additional-attributes"),
  tableColumnAdditionalAttributesRetrievalStrategy("table-column-additional-attributes"),
  tablePrivilegesRetrievalStrategy("tableprivileges"),
  tablesRetrievalStrategy("tables"),
  triggersRetrievalStrategy("triggers"),
  tableConstraintsRetrievalStrategy("table-constraints"),
  tableConstraintColumnsRetrievalStrategy("table-constraint-columns"),
  tableCheckConstraintsRetrievalStrategy("table-check-constraints"),
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
