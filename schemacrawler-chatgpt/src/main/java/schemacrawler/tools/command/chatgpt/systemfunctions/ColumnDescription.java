package schemacrawler.tools.command.chatgpt.systemfunctions;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public final class ColumnDescription {

  private String name;
  public String dataType;
  public String remarks;

  @JsonProperty("type")
  public String getDataType() {
    return dataType;
  }

  public String getName() {
    return name;
  }

  public String getRemarks() {
    return remarks;
  }

  public void setDataType(final String dataType) {
    this.dataType = dataType;
  }

  @JsonSetter(nulls = Nulls.AS_EMPTY)
  public void setName(final String name) {
    this.name = name;
  }

  @JsonSetter(nulls = Nulls.AS_EMPTY)
  public void setRemarks(final String remarks) {
    this.remarks = remarks;
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
