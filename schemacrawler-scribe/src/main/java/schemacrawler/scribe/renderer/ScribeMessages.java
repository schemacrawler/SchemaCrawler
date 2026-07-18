/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.renderer;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.ResourceBundle;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;

/**
 * Typed wrapper over the localized Scribe message bundle. Renderers and templates must use these
 * typed accessors, rather than calling {@link ResourceBundle#getString(String)} directly, so that
 * every user-visible string routes through localization.
 */
public final class ScribeMessages {

  private static final String BUNDLE_NAME = "schemacrawler.scribe.i18n.ScribeMessages";

  private final ResourceBundle bundle;

  public ScribeMessages(final Locale locale) {
    try {
      requireNonNull(locale, "No locale provided");
      bundle = ResourceBundle.getBundle(BUNDLE_NAME, locale);
    } catch (final Exception e) {
      throw new InternalRuntimeException("Could not load resource bundle", e);
    }
  }

  public String headerAttribute() {
    return message("header.attribute");
  }

  public String headerAutoIncremented() {
    return message("header.auto_incremented");
  }

  public String headerDefault() {
    return message("header.default");
  }

  public String headerGenerated() {
    return message("header.generated");
  }

  public String headerMode() {
    return message("header.mode");
  }

  public String headerName() {
    return message("header.name");
  }

  public String headerNullable() {
    return message("header.nullable");
  }

  public String headerRemarks() {
    return message("header.remarks");
  }

  public String headerType() {
    return message("header.type");
  }

  public String headerValue() {
    return message("header.value");
  }

  public String labelBodyType() {
    return message("label.body_type");
  }

  public String labelChildFkCount() {
    return message("label.child_fk_count");
  }

  public String labelColumnCount() {
    return message("label.column_count");
  }

  public String labelDatabaseProduct() {
    return message("label.database_product");
  }

  public String labelDatabaseVersion() {
    return message("label.database_version");
  }

  public String labelDatabaseSchema() {
    return message("label.database_schema");
  }

  public String labelEntityModelType() {
    return message("label.entity_model_type");
  }

  public String labelForeignKeyCount() {
    return message("label.foreign_key_count");
  }

  public String labelFunction() {
    return message("label.function");
  }

  public String labelReferencedBy() {
    return message("label.referenced_by");
  }

  public String labelReferences() {
    return message("label.references");
  }

  public String labelReturnType() {
    return message("label.return_type");
  }

  public String labelRoutine() {
    return message("label.routine");
  }

  public String labelRoutines() {
    return message("label.routines");
  }

  public String labelRowCount() {
    return message("label.row_count");
  }

  public String labelStoredProcedure() {
    return message("label.stored_procedure");
  }

  public String labelTable() {
    return message("label.table");
  }

  public String labelTables() {
    return message("label.tables");
  }

  public String labelTriggerCount() {
    return message("label.trigger_count");
  }

  public String labelType() {
    return message("label.type");
  }

  public String labelView() {
    return message("label.view");
  }

  public String labelViews() {
    return message("label.views");
  }

  public String lintSeverityCritical() {
    return message("lint.severity.critical");
  }

  public String lintSeverityHigh() {
    return message("lint.severity.high");
  }

  public String lintSeverityLow() {
    return message("lint.severity.low");
  }

  public String lintSeverityMedium() {
    return message("lint.severity.medium");
  }

  public String navLint() {
    return message("nav.lint");
  }

  public String navRoutines() {
    return message("nav.routines");
  }

  public String navTables() {
    return message("nav.tables");
  }

  public String sectionAttributes() {
    return message("section.attributes");
  }

  public String sectionCheckConstraints() {
    return message("section.check_constraints");
  }

  public String sectionColumns() {
    return message("section.columns");
  }

  public String sectionCrossReferences() {
    return message("section.cross_references");
  }

  public String sectionDefinition() {
    return message("section.definition");
  }

  public String headerDescription() {
    return message("header.description");
  }

  public String sectionDiagram() {
    return message("section.diagram");
  }

  public String sectionForeignKeys() {
    return message("section.foreign_keys");
  }

  public String sectionIndexes() {
    return message("section.indexes");
  }

  public String sectionLintIssues() {
    return message("section.lint_issues");
  }

  public String sectionMetadata() {
    return message("section.metadata");
  }

  public String sectionParameters() {
    return message("section.parameters");
  }

  public String sectionPrimaryKey() {
    return message("section.primary_key");
  }

  public String sectionTriggers() {
    return message("section.triggers");
  }

  public String triggerAttributeActionOrder() {
    return message("trigger.attribute.action_order");
  }

  public String triggerAttributeActionStatement() {
    return message("trigger.attribute.action_statement");
  }

  public String triggerAttributeCondition() {
    return message("trigger.attribute.condition");
  }

  public String triggerAttributeEvents() {
    return message("trigger.attribute.events");
  }

  public String triggerAttributeOrientation() {
    return message("trigger.attribute.orientation");
  }

  public String triggerAttributeTiming() {
    return message("trigger.attribute.timing");
  }

  public String valueEntityModelTypeBridgeTable() {
    return message("value.entity_model_type.bridge_table");
  }

  public String valueEntityModelTypeNonEntity() {
    return message("value.entity_model_type.non_entity");
  }

  public String valueEntityModelTypeStrongEntity() {
    return message("value.entity_model_type.strong_entity");
  }

  public String valueEntityModelTypeSubtype() {
    return message("value.entity_model_type.subtype");
  }

  public String valueEntityModelTypeWeakEntity() {
    return message("value.entity_model_type.weak_entity");
  }

  public String valueNa() {
    return message("value.na");
  }

  public String valueUniqueIndex() {
    return message("value.unique_index");
  }

  public String valueUnknown() {
    return message("value.unknown");
  }

  public String valueYes() {
    return message("value.yes");
  }

  private String message(final String key) {
    return bundle.getString(key);
  }
}
