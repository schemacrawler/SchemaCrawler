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
  proceduresRetrievalStrategy("procedures"),
  procedureParametersRetrievalStrategy("procedureparameters"),
  tableColumnPrivilegesRetrievalStrategy("tablecolumnprivileges"),
  tableColumnsRetrievalStrategy("tablecolumns"),
  tablePrivilegesRetrievalStrategy("tableprivileges"),
  tablesRetrievalStrategy("tables"),
  typeInfoRetrievalStrategy("typeinfo"),
  ;

  private final String key;

  SchemaInfoMetadataRetrievalStrategy(final String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
