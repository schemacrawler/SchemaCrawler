package schemacrawler.tools.command.chatgpt.functions;

import static schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionParameters.DatabaseObjectType.TABLES;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class DatabaseObjectListFunctionParameters implements FunctionParameters {

  public enum DatabaseObjectType {
    TABLES("tables"),
    ROUTINES("routines"),
    SCHEMAS("schemas"),
    SEQUENCES("sequences"),
    SYNONYMS("synonyms");

    private final String readableString;

    DatabaseObjectType(final String readableString) {
      this.readableString = readableString;
    }

    public String toReadableString() {
      return readableString;
    }
  }

  @JsonPropertyDescription(
      "Type of database object to list, like tables, routines (that is, functions and stored procedures), schemas (that is, catalogs), sequences, or synonyms.")
  private DatabaseObjectType databaseObjectType;

  public DatabaseObjectType getDatabaseObjectType() {
    if (databaseObjectType == null) {
      return TABLES;
    }
    return databaseObjectType;
  }

  public void setDatabaseObjectType(final DatabaseObjectType databaseObjectType) {
    this.databaseObjectType = databaseObjectType;
  }
}
