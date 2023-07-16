package schemacrawler.tools.command.chatgpt.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class TableReferencesFunctionParameters implements FunctionParameters {

  public enum TableReferenceType {
    all,
    parent,
    child;
  }

  @JsonPropertyDescription(
      "Part of the name of database table to find. For example, 'ABC' is a part of 'QWEABCXYZ'.")
  @JsonProperty(required = true)
  private String tableNameContains;

  @JsonPropertyDescription(
      "The type of related tables requested - either child tables or parent tables, or both types (all relationships).")
  private TableReferenceType tableReferenceType;

  public String getTableNameContains() {
    return tableNameContains;
  }

  public TableReferenceType getTableReferenceType() {
    if (tableReferenceType == null) {
      return TableReferenceType.all;
    }
    return tableReferenceType;
  }

  public void setTableNameContains(final String tableNameContains) {
    this.tableNameContains = tableNameContains;
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
