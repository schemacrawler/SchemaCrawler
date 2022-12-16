/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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

/** Basic SchemaCrawler executor for text output. */
public final class SchemaTextRenderer extends BaseSchemaCrawlerCommand<SchemaTextOptions> {

  public SchemaTextRenderer(final String command) {
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
      schemaTextDetailType = SchemaTextDetailType.valueOf(command);
    } catch (final IllegalArgumentException e) {
      schemaTextDetailType = SchemaTextDetailType.schema;
    }
    return schemaTextDetailType;
  }

  private SchemaTraversalHandler getSchemaTraversalHandler() {
    final SchemaTextDetailType schemaTextDetailType = getSchemaTextDetailType();
    final SchemaTraversalHandler formatter;

    final String identifierQuoteString = identifiers.getIdentifierQuoteString();
    if (schemaTextDetailType == SchemaTextDetailType.list) {
      formatter =
          new SchemaListFormatter(
              schemaTextDetailType, commandOptions, outputOptions, identifierQuoteString);
    } else {
      formatter =
          new SchemaTextFormatter(
              schemaTextDetailType, commandOptions, outputOptions, identifierQuoteString);
    }

    return formatter;
  }
}
