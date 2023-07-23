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

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.chatgpt.FunctionParameters;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class DatabaseObjectDescriptionFunctionParameters implements FunctionParameters {

  public enum DatabaseObjectsScope {
    NONE,
    SEQUENCES,
    SYNONYMS,
    ROUTINES,
    ;
  }

  @JsonPropertyDescription("Name of database object to describe.")
  private String databaseObjectName;

  @JsonPropertyDescription(
      "Indicates what details of database objects to show - sequences, synonyms, or routines (that is, stored procedures or functions).")
  private DatabaseObjectsScope databaseObjectsScope;

  public String getDatabaseObjectName() {
    return databaseObjectName;
  }

  public DatabaseObjectsScope getDatabaseObjectsScope() {
    if (databaseObjectsScope == null) {
      return DatabaseObjectsScope.NONE;
    }
    return databaseObjectsScope;
  }

  public void setDatabaseObjectName(final String databaseObjectName) {
    this.databaseObjectName = databaseObjectName;
  }

  public void setDatabaseObjectsScope(final DatabaseObjectsScope databaseObjectsScope) {
    this.databaseObjectsScope = databaseObjectsScope;
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
