package schemacrawler.test.utility.crawl;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.Objects.requireNonNull;
import schemacrawler.schema.ActionOrientationType;
import schemacrawler.schema.ConditionTimingType;
import schemacrawler.schema.EventManipulationType;
import schemacrawler.schema.NamedObject;
import schemacrawler.schema.NamedObjectKey;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Table;
import schemacrawler.schema.Trigger;
import schemacrawler.schemacrawler.Identifiers;

public class LightTrigger implements Trigger {

  private static final long serialVersionUID = -2552665161195438344L;

  private final Schema schema;
  private final String name;
  private final Table table;
  private String actionStatement;

  public LightTrigger(final Table table, final String name) {
    this.table = requireNonNull(table, "No table provided");
    schema = table.getSchema();
    this.name = name;
  }

  public void setActionStatement(String actionStatement) {
    this.actionStatement = actionStatement;
  }

  @Override
  public String getShortName() {
    return name;
  }

  @Override
  public boolean isParentPartial() {
    return true;
  }

  @Override
  public Schema getSchema() {
    return schema;
  }

  @Override
  public void withQuoting(Identifiers identifiers) {}

  @Override
  public String getFullName() {
    return name;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public NamedObjectKey key() {
    return null;
  }

  @Override
  public int compareTo(NamedObject o) {
    return 0;
  }

  @Override
  public <T> T getAttribute(String name) {
    return null;
  }

  @Override
  public <T> T getAttribute(String name, T defaultValue) throws ClassCastException {
    return null;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return null;
  }

  @Override
  public boolean hasAttribute(String name) {
    return false;
  }

  @Override
  public <T> Optional<T> lookupAttribute(String name) {
    return Optional.empty();
  }

  @Override
  public void removeAttribute(String name) {}

  @Override
  public <T> void setAttribute(String name, T value) {}

  @Override
  public String getRemarks() {
    return null;
  }

  @Override
  public boolean hasRemarks() {
    return false;
  }

  @Override
  public void setRemarks(String remarks) {}

  @Override
  public Table getParent() {
    return table;
  }

  @Override
  public String getActionCondition() {
    return null;
  }

  @Override
  public int getActionOrder() {
    return 0;
  }

  @Override
  public ActionOrientationType getActionOrientation() {
    return null;
  }

  @Override
  public String getActionStatement() {
    return actionStatement;
  }

  @Override
  public ConditionTimingType getConditionTiming() {
    return null;
  }

  @Override
  public Set<EventManipulationType> getEventManipulationTypes() {
    return null;
  }
}
