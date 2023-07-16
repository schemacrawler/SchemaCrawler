package schemacrawler.tools.command.chatgpt.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class LintFunctionParameters implements FunctionParameters {

  @JsonPropertyDescription(
      "Part of the name of database table to find. For example, 'ABC' is a part of 'QWEABCXYZ'.")
  @JsonProperty(required = true)
  private String tableNameContains;

  public String getTableNameContains() {
    return tableNameContains;
  }

  public void setTableNameContains(final String tableNameContains) {
    this.tableNameContains = tableNameContains;
  }
}
