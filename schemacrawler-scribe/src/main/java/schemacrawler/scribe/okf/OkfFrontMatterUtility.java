/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import static schemacrawler.loader.utility.TableRowCountsUtility.hasRowCount;

import java.net.URI;
import java.net.URISyntaxException;
import schemacrawler.loader.utility.TableRowCountsUtility;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schema.TypedObject;
import schemacrawler.scribe.okf.frontmatter.CountsRecord;
import schemacrawler.scribe.okf.frontmatter.DatabaseObjectDescription;
import schemacrawler.scribe.okf.frontmatter.SchemaCrawlerActor;
import schemacrawler.scribe.okf.frontmatter.TableAttributesRecord;
import schemacrawler.scribe.okf.frontmatter.okf.TrustTier;
import schemacrawler.scribe.okf.frontmatter.okf.VerifiedRecord;
import schemacrawler.utility.MetaDataUtility;
import us.fatehi.utility.UtilityMarker;

@UtilityMarker
class OkfFrontMatterUtility {

  static DatabaseObjectDescription objectDescription(final DatabaseObject databaseObject) {
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

    URI resource;
    try {
      final String context;
      if (databaseObject instanceof Table) {
        context = "tables";
      } else if (databaseObject instanceof Routine) {
        context = "routines";
      } else {
        context = "unknowns";
      }
      final String path = "/" + String.join("/", databaseObject.getSchema().toString(), name);
      resource = new URI("catalog", context, path, null);
    } catch (final URISyntaxException e) {
      resource = null;
    }

    return new DatabaseObjectDescription(
        simpleTypeName, completeType, schemaName, name, fullName, description, intro, resource);
  }

  static CountsRecord routineCounts(final Routine routine) {
    if (routine == null) {
      return new CountsRecord();
    }
    return new CountsRecord(null, null, null, null, null, routine.getParameters().size());
  }

  static TableAttributesRecord tableAttributes(final Table table, final boolean isBridgeTable) {
    if (table == null) {
      return new TableAttributesRecord();
    }
    return new TableAttributesRecord(
        !table.hasPrimaryKey(),
        table.isSelfReferencing(),
        table.hasTriggers(),
        !hasRowCount(table),
        isBridgeTable);
  }

  static CountsRecord tableCounts(final Table table) {
    if (table == null) {
      return null;
    }
    final Long rowCount =
        TableRowCountsUtility.hasRowCount(table) ? TableRowCountsUtility.getRowCount(table) : null;
    return new CountsRecord(
        table.getColumns().size(),
        table.getReferencedTables().size(),
        table.getIndexes().size(),
        table.getTriggers().size(),
        rowCount,
        null);
  }

  static VerifiedRecord verified() {
    return new VerifiedRecord(TrustTier.machine_confirmed, new SchemaCrawlerActor());
  }

  private OkfFrontMatterUtility() {
    // Prevent instantiation
  }
}
