package schemacrawler.tools.command.serialize.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"databaseProductName", "tables"})
public final class CatalogDocument implements Serializable {

  private static final long serialVersionUID = -1937966351313941597L;

  private final List<TableDocument> tables;
  private final String databaseProductName;

  public CatalogDocument(final String databaseProductName) {
    tables = new ArrayList<>();
    this.databaseProductName = databaseProductName;
  }

  public void addTable(final TableDocument table) {
    if (table != null) {
      tables.add(table);
    }
  }

  @JsonProperty("db")
  public String getDatabaseProductName() {
    return databaseProductName;
  }

  @JsonProperty("tables")
  public List<TableDocument> getTables() {
    return tables;
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
