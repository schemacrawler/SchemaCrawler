package schemacrawler.tools.command.chatgpt.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
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

  @JsonPropertyDescription(
      "Part of the name of database object to find. For example, 'ABC' is a part of 'QWEABCXYZ'.")
  @JsonProperty(required = true)
  private String databaseObjectNameContains;

  @JsonPropertyDescription(
      "Indicates what details of database objects to show - sequences, synonyms, or routines (that is, stored procedures or functions).")
  private DatabaseObjectsScope databaseObjectsScope;

  public String getDatabaseObjectNameContains() {
    return databaseObjectNameContains;
  }

  public DatabaseObjectsScope getDatabaseObjectsScope() {
    if (databaseObjectsScope == null) {
      return DatabaseObjectsScope.NONE;
    }
    return databaseObjectsScope;
  }

  public void setDatabaseObjectNameContains(final String databaseObjectNameContains) {
    this.databaseObjectNameContains = databaseObjectNameContains;
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
