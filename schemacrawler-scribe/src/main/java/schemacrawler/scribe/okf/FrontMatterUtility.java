/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.loader.utility.TableRowCountsUtility.getRowCount;
import static schemacrawler.loader.utility.TableRowCountsUtility.hasRowCount;

import java.net.URI;
import schemacrawler.loader.utility.TableRowCountsUtility;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schema.TypedObject;
import schemacrawler.scribe.okf.frontmatter.DatabaseObjectDescription;
import schemacrawler.scribe.okf.frontmatter.okf.SchemaCrawlerActor;
import schemacrawler.scribe.okf.frontmatter.okf.TrustTier;
import schemacrawler.scribe.okf.frontmatter.okf.Verified;
import schemacrawler.scribe.okf.frontmatter.schemacrawler.Counts;
import schemacrawler.scribe.okf.frontmatter.schemacrawler.TableAttributes;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
public class FrontMatterUtility {

  public static DatabaseObjectDescription objectDescription(final DatabaseObject databaseObject) {
    if (databaseObject == null) {
      return new DatabaseObjectDescription();
    }
    final String simpleTypeName = MetaDataUtility.getSimpleTypeName(databaseObject).toString();
    final String schemaName = databaseObject.getSchema().getFullName();
    final String name = databaseObject.getName();
    final String fullName = databaseObject.getFullName();

    final String intro;
    final String description;
    if (databaseObject.hasRemarks()) {
      intro = databaseObject.getRemarks();
      description = intro;
    } else {
      intro = "Description of %s %s".formatted(simpleTypeName, fullName);
      description = "Description of " + simpleTypeName;
    }

    final String completeType;
    if (databaseObject instanceof final TypedObject typedObject) {
      completeType = typedObject.getType().toString();
    } else {
      completeType = null;
    }

    final URI resource = MetaDataUtility.getDatabaseObjectUri(databaseObject);

    return new DatabaseObjectDescription(
        simpleTypeName, completeType, schemaName, name, fullName, description, intro, resource);
  }

  public static Counts routineCounts(final Routine routine) {
    if (routine == null) {
      return new Counts();
    }
    return new Counts(null, null, null, null, null, routine.getParameters().size());
  }

  public static TableAttributes tableAttributes(final Table table, final boolean isBridgeTable) {
    if (table == null) {
      return new TableAttributes();
    }
    return new TableAttributes(
        !table.hasPrimaryKey(),
        !table.hasForeignKeys(),
        !table.hasIndexes(),
        table.isSelfReferencing(),
        table.hasTriggers(),
        hasRowCount(table) && getRowCount(table) == 0,
        isBridgeTable);
  }

  public static Counts tableCounts(final Table table) {
    if (table == null) {
      return null;
    }
    final Long rowCount =
        TableRowCountsUtility.hasRowCount(table) ? TableRowCountsUtility.getRowCount(table) : null;
    return new Counts(
        table.getColumns().size(),
        table.getReferencedTables().size(),
        table.getIndexes().size(),
        table.getTriggers().size(),
        rowCount,
        null);
  }

  public static Verified verified() {
    return new Verified(TrustTier.machine_confirmed, new SchemaCrawlerActor());
  }

  private FrontMatterUtility() {
    // Prevent instantiation
  }
}
