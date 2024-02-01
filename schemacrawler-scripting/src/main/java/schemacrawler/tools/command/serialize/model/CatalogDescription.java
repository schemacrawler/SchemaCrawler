package schemacrawler.tools.command.serialize.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class CatalogDescription {

  private final List<TableDescription> tables;
  private final String databaseProductName;

  public CatalogDescription(final String databaseProductName) {
    this.tables = new ArrayList<>();
    this.databaseProductName = databaseProductName;
  }

  public void addTable(final TableDescription table) {
    if (table != null) {
      this.tables.add(table);
    }
  }

  @JsonProperty("db")
  public String getDatabaseProductName() {
    return this.databaseProductName;
  }

  @JsonProperty("tables")
  public List<TableDescription> getTables() {
    return this.tables;
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
