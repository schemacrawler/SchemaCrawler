package schemacrawler.tools.command.chatgpt.functions;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class TableDecriptionFunctionParameters implements FunctionParameters {

  public enum TableDescriptionScope {
    DEFAULT("details"),
    COLUMNS("columns"),
    PRIMARY_KEY("primary key"),
    INDEXES("indexes"),
    FOREIGN_KEYS("foreign keys"),
    TRIGGERS("triggers");

    private final String readableString;

    TableDescriptionScope(final String readableString) {
      this.readableString = readableString;
    }

    public String toReadableString() {
      return readableString;
    }
  }

  @JsonPropertyDescription(
      "Part of the name of database table to find. For example, 'ABC' is a part of 'QWEABCXYZ'.")
  @JsonProperty(required = true)
  private String tableNameContains;

  @JsonPropertyDescription(
      "Indicates what details of the database table to show - columns, primary key, indexes, foreign keys, or triggers.")
  private TableDescriptionScope descriptionScope;

  public TableDescriptionScope getDescriptionScope() {
    if (descriptionScope == null) {
      return TableDescriptionScope.DEFAULT;
    }
    return descriptionScope;
  }

  public String getTableNameContains() {
    return tableNameContains;
  }

  public void setDescriptionScope(final TableDescriptionScope descriptionScope) {
    this.descriptionScope = descriptionScope;
  }

  public void setTableNameContains(final String tableNameContains) {
    this.tableNameContains = tableNameContains;
  }
}
