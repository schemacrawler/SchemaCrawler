package schemacrawler.tools.command.chatgpt.functions;

import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.ALL;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

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
