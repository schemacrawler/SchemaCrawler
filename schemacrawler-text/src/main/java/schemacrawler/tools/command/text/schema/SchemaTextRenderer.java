/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.schema;

import schemacrawler.tools.command.text.schema.options.SchemaTextDetailType;
import schemacrawler.tools.command.text.schema.options.SchemaTextOptions;
import schemacrawler.tools.executable.BaseSchemaCrawlerCommand;
import schemacrawler.tools.text.formatter.schema.SchemaListFormatter;
import schemacrawler.tools.text.formatter.schema.SchemaTextFormatter;
import schemacrawler.tools.traversal.SchemaTraversalHandler;
import schemacrawler.tools.traversal.SchemaTraverser;
import schemacrawler.utility.NamedObjectSort;
import us.fatehi.utility.property.PropertyName;

/** Basic SchemaCrawler executor for text output. */
public final class SchemaTextRenderer extends BaseSchemaCrawlerCommand<SchemaTextOptions> {

  public SchemaTextRenderer(final PropertyName command) {
    super(command);
  }

  @Override
  public void checkAvailability() {
    // Text rendering is always available
  }

  @Override
  public void execute() {
    checkCatalog();

    final SchemaTraversalHandler formatter = getSchemaTraversalHandler();

    final SchemaTraverser traverser = new SchemaTraverser();
    traverser.setCatalog(catalog);
    traverser.setHandler(formatter);
    traverser.setTablesComparator(
        NamedObjectSort.getNamedObjectSort(commandOptions.isAlphabeticalSortForTables()));
    traverser.setRoutinesComparator(
        NamedObjectSort.getNamedObjectSort(commandOptions.isAlphabeticalSortForRoutines()));

    traverser.traverse();
  }

  @Override
  public boolean usesConnection() {
    return false;
  }

  private SchemaTextDetailType getSchemaTextDetailType() {
    SchemaTextDetailType schemaTextDetailType;
    try {
      schemaTextDetailType = SchemaTextDetailType.valueOf(command.getName());
    } catch (final IllegalArgumentException e) {
      schemaTextDetailType = SchemaTextDetailType.schema;
    }
    return schemaTextDetailType;
  }

  private SchemaTraversalHandler getSchemaTraversalHandler() {
    final SchemaTextDetailType schemaTextDetailType = getSchemaTextDetailType();
    final SchemaTraversalHandler formatter;

    if (schemaTextDetailType == SchemaTextDetailType.list) {
      formatter =
          new SchemaListFormatter(schemaTextDetailType, commandOptions, outputOptions, identifiers);
    } else {
      formatter =
          new SchemaTextFormatter(schemaTextDetailType, commandOptions, outputOptions, identifiers);
    }

    return formatter;
  }
}
