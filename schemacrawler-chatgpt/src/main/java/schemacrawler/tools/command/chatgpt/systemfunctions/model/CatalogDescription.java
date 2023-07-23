package schemacrawler.tools.command.chatgpt.systemfunctions.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class CatalogDescription {

  private final List<SchemaDescription> schemas;

  public CatalogDescription() {
    schemas = new ArrayList<>();
  }

  public void addSchema(final SchemaDescription schema) {
    if (schema != null) {
      schemas.add(schema);
    }
  }

  public List<SchemaDescription> getSchemas() {
    return schemas;
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
