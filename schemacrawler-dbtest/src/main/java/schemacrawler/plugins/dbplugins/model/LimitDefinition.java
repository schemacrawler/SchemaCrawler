/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.plugins.dbplugins.model;

import static us.fatehi.utility.Utility.isBlank;

import schemacrawler.plugins.dbplugins.yaml.JsonUtility;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.annotation.JsonNaming;

/** Represents catalog and schema limit patterns. */
@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public record LimitDefinition(
    String includeSchemas, String excludeSchemas, String includeCatalogs, String excludeCatalogs) {

  public LimitDefinition() {
    this(null, null, null, null);
  }

  public LimitDefinition {
    includeSchemas = isBlank(includeSchemas) ? null : includeSchemas;
    excludeSchemas = isBlank(excludeSchemas) ? null : excludeSchemas;
    includeCatalogs = isBlank(includeCatalogs) ? null : includeCatalogs;
    excludeCatalogs = isBlank(excludeCatalogs) ? null : excludeCatalogs;
  }

  @Override
  public String toString() {
    return JsonUtility.mapper.writeValueAsString(this);
  }
}
