/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.chatgpt.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.chatgpt.FunctionParameters;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class TableReferencesFunctionParameters implements FunctionParameters {

  public enum TableReferenceType {
    ALL,
    PARENT,
    CHILD;
  }

  @JsonPropertyDescription("Name of database table for which to show references.")
  @JsonProperty(required = true)
  private String tableName;

  @JsonPropertyDescription(
      "The type of related tables requested - either child tables or parent tables, or both types (all relationships).")
  private TableReferenceType tableReferenceType;

  public String getTableName() {
    return tableName;
  }

  public TableReferenceType getTableReferenceType() {
    if (tableReferenceType == null) {
      return TableReferenceType.ALL;
    }
    return tableReferenceType;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
  }

  public void setTableReferenceType(final TableReferenceType tableReferenceType) {
    this.tableReferenceType = tableReferenceType;
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      return super.toString();
    }
  }
}
