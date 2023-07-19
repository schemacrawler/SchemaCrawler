package schemacrawler.tools.command.chatgpt.functions;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class DatabaseObjectDescriptionFunctionParameters implements FunctionParameters {

  public enum DatabaseObjectsScope {
    NONE("none"),
    SEQUENCES("squences"),
    SYNONYMS("synonyms"),
    ROUTINES("routines"),
    ;

    private final String readableString;

    DatabaseObjectsScope(final String readableString) {
      this.readableString = readableString;
    }

    public String toReadableString() {
      return readableString;
    }
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

  public void setDatabaseObjectName(final String databaseObjectNameContains) {
    this.databaseObjectName = databaseObjectNameContains;
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
