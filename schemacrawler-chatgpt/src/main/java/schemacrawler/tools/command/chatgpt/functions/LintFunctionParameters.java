package schemacrawler.tools.command.chatgpt.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class LintFunctionParameters implements FunctionParameters {

  @JsonPropertyDescription(
      "Name of database table for which to find design issues. "
          + "Use a blank value to find design issues for all tables.")
  @JsonProperty(required = true)
  private String tableName;

  public String getTableName() {
    return tableName;
  }

  public void setTableName(final String tableNameContains) {
    this.tableName = tableNameContains;
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
