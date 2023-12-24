/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.ALL;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import schemacrawler.tools.command.chatgpt.FunctionParameters;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class DatabaseObjectListFunctionParameters implements FunctionParameters {

  public enum DatabaseObjectType {
    ALL,
    TABLES,
    ROUTINES,
    SEQUENCES,
    SYNONYMS;
  }

  @JsonPropertyDescription(
      "Type of database object to list, like tables, routines (that is, functions and stored procedures), schemas (that is, catalogs), sequences, or synonyms.")
  private DatabaseObjectType databaseObjectType;

  public DatabaseObjectType getDatabaseObjectType() {
    if (databaseObjectType == null) {
      return ALL;
    }
    return databaseObjectType;
  }

  public void setDatabaseObjectType(final DatabaseObjectType databaseObjectType) {
    this.databaseObjectType = databaseObjectType;
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
