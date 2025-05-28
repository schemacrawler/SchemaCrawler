package schemacrawler.tools.command.serialize.model;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Index;
import schemacrawler.schema.IndexColumn;
import schemacrawler.schema.PrimaryKey;
import schemacrawler.schema.TableConstraintColumn;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"name", "columns", "is-unique"})
public final class IndexDocument implements Serializable {

  private static final long serialVersionUID = 1873929712139211255L;

  private final String name;
  private final List<String> columns;
  private final boolean isUnique;

  public IndexDocument(final Index index) {
    requireNonNull(index, "No index provided");

    name = index.getName();
    columns = index.getColumns().stream().map(IndexColumn::getName).collect(Collectors.toList());
    isUnique = index.isUnique();
  }

  public IndexDocument(final PrimaryKey primaryKey) {
    requireNonNull(primaryKey, "No index provided");

    name = primaryKey.getName();
    columns =
        primaryKey.getConstrainedColumns().stream()
            .map(TableConstraintColumn::getName)
            .collect(Collectors.toList());
    isUnique = true;
  }

  public List<String> getColumns() {
    return columns;
  }

  public String getName() {
    return name;
  }

  public boolean isUnique() {
    return isUnique;
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
