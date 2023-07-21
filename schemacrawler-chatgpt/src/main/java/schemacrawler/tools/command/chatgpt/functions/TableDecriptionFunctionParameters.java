package schemacrawler.tools.command.chatgpt.functions;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
public class TableDecriptionFunctionParameters implements FunctionParameters {

  public enum TableDescriptionScope {
    DEFAULT,
    COLUMNS,
    PRIMARY_KEY,
    INDEXES,
    FOREIGN_KEYS,
    TRIGGERS;
  }

  @JsonPropertyDescription("Name of database table or view to describe.")
  private String tableName;

  @JsonPropertyDescription(
      "Indicates what details of the database table or view to show - columns, primary key, indexes, foreign keys, or triggers.")
  private TableDescriptionScope descriptionScope;

  public TableDescriptionScope getDescriptionScope() {
    if (descriptionScope == null) {
      return TableDescriptionScope.DEFAULT;
    }
    return descriptionScope;
  }

  public String getTableName() {
    return tableName;
  }

  public void setDescriptionScope(final TableDescriptionScope descriptionScope) {
    this.descriptionScope = descriptionScope;
  }

  public void setTableName(final String tableName) {
    this.tableName = tableName;
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
