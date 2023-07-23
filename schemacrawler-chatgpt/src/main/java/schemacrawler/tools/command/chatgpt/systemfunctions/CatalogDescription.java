package schemacrawler.tools.command.chatgpt.systemfunctions;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class CatalogDescription {

  private final List<TableDescription> tables;

  public CatalogDescription() {
    tables = new ArrayList<>();
  }

  public void addTable(final TableDescription table) {
    if (table != null) {
      tables.add(table);
    }
  }

  public List<TableDescription> getTables() {
    return tables;
  }
}
