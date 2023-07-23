package schemacrawler.tools.command.chatgpt.systemfunctions;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class SchemaDescription {

  private String name;
  private final List<TableDescription> tables;

  public SchemaDescription() {
    tables = new ArrayList<>();
  }

  public void addTable(final TableDescription table) {
    if (table != null) {
      tables.add(table);
    }
  }

  public String getName() {
    return name;
  }

  public List<TableDescription> getTables() {
    return tables;
  }

  public void setName(final String name) {
    this.name = name;
  }
}
