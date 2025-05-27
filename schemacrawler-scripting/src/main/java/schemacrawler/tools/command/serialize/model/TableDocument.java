package schemacrawler.tools.command.serialize.model;

import static schemacrawler.tools.command.serialize.model.AdditionalTableDetails.DEFINIITION;
import static schemacrawler.tools.command.serialize.model.AdditionalTableDetails.INDEXES;
import static schemacrawler.tools.command.serialize.model.AdditionalTableDetails.TRIGGERS;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.Utility.trimToEmpty;
import schemacrawler.schema.Column;
import schemacrawler.schema.ColumnReference;
import schemacrawler.schema.ForeignKey;
import schemacrawler.schema.Index;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;

@JsonNaming(PropertyNamingStrategies.KebabCaseStrategy.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"schema", "table", "remarks", "primary-key", "columns", "indexes", "attributes",
    "definition"})
public final class TableDocument implements Serializable {

  private static final long serialVersionUID = 1873929712139211255L;

  public static Map<AdditionalTableDetails, Boolean> allTableDetails() {
    final Map<AdditionalTableDetails, Boolean> details;
    details = new EnumMap<>(AdditionalTableDetails.class);

    for (final AdditionalTableDetails additionalTableDetails : AdditionalTableDetails.values()) {
      if (!details.containsKey(additionalTableDetails)) {
        details.put(additionalTableDetails, true);
      }
    }
    return details;
  }

  private final String schemaName;
  private final String tableName;
  private final String remarks;
  private final IndexDocument primaryKey;
  private final List<ColumnDocument> columns;
  private final Collection<IndexDocument> indexes;
  private final List<TriggerDocument> triggers;
  private final Map<String, String> attributes;

  private final String definition;

  TableDocument(final Table table, final Map<AdditionalTableDetails, Boolean> tableDetails) {
    Objects.requireNonNull(table, "No table provided");
    final Map<AdditionalTableDetails, Boolean> details = defaults(tableDetails);

    final String schemaName = table.getSchema().getFullName();
    this.schemaName = trimToEmpty(schemaName);

    tableName = table.getName();

    if (table.hasPrimaryKey()) {
      primaryKey = new IndexDocument(table.getPrimaryKey());
    } else {
      primaryKey = null;
    }

    final Map<String, Column> referencedColumns = mapReferencedColumns(table);
    columns = new ArrayList<>();
    for (final Column column : table.getColumns()) {
      final ColumnDocument columnDocument =
          new ColumnDocument(column, referencedColumns.get(column.getName()));
      columns.add(columnDocument);
    }

    if (details.get(INDEXES)) {
      indexes = new ArrayList<>();
      for (final Index index : table.getIndexes()) {
        indexes.add(new IndexDocument(index));
      }
    } else {
      indexes = null;
    }

    if (table.hasRemarks()) {
      final String remarks = table.getRemarks();
      this.remarks = trimToEmpty(remarks);
    } else {
      remarks = null;
    }

    if (details.get(TRIGGERS) && table.hasTriggers()) {
      triggers = new ArrayList<>();
      for (final Trigger trigger : table.getTriggers()) {
        triggers.add(new TriggerDocument(trigger));
      }
    } else {
      triggers = null;
    }

    if (details.get(DEFINIITION) && table.hasDefinition()) {
      definition = table.getDefinition();
    } else {
      definition = null;
    }

    if (details.get(AdditionalTableDetails.ATTRIBUTES)) {
      attributes = new HashMap<>();
      table.getAttributes().entrySet().stream()
          .forEach(entry -> attributes.put(entry.getKey(), String.valueOf(entry.getValue())));
    } else {
      attributes = null;
    }
  }

  public Map<String, String> getAttributes() {
    return attributes;
  }

  public List<ColumnDocument> getColumns() {
    return columns;
  }

  public String getDefinition() {
    return definition;
  }

  public Collection<IndexDocument> getIndexes() {
    return indexes;
  }

  public IndexDocument getPrimaryKey() {
    return primaryKey;
  }

  public String getRemarks() {
    return remarks;
  }

  @JsonProperty("schema")
  public String getSchema() {
    return schemaName;
  }

  @JsonProperty("table")
  public String getTableName() {
    return tableName;
  }

  public String toJson() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      return super.toString();
    }
  }

  @Override
  public String toString() {
    return toJson();
  }

  private Map<AdditionalTableDetails, Boolean> defaults(
      final Map<AdditionalTableDetails, Boolean> tableDetails) {
    final Map<AdditionalTableDetails, Boolean> details;
    if (tableDetails == null) {
      details = new EnumMap<>(AdditionalTableDetails.class);
    } else {
      details = tableDetails;
    }

    for (final AdditionalTableDetails additionalTableDetails : AdditionalTableDetails.values()) {
      if (!details.containsKey(additionalTableDetails)) {
        details.put(additionalTableDetails, false);
      }
    }
    return details;
  }

  private Map<String, Column> mapReferencedColumns(final Table table) {
    requireNonNull(table, "No table provided");

    final Map<String, Column> referencedColumns = new HashMap<>();
    for (final ForeignKey foreignKey : table.getImportedForeignKeys()) {
      final List<ColumnReference> columnReferences = foreignKey.getColumnReferences();
      for (final ColumnReference columnReference : columnReferences) {
        referencedColumns.put(columnReference.getForeignKeyColumn().getName(),
            columnReference.getPrimaryKeyColumn());
      }
    }
    return referencedColumns;
  }
}
