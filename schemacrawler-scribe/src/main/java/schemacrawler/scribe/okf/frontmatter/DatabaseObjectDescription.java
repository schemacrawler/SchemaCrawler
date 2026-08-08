/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import java.net.URI;
import java.net.URISyntaxException;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Routine;
import schemacrawler.schema.Table;
import schemacrawler.schema.TypedObject;
import schemacrawler.utility.MetaDataUtility;

public record DatabaseObjectDescription(
    String simpleTypeName,
    String completeType,
    String name,
    String fullName,
    String description,
    String intro,
    URI resource) {

  public DatabaseObjectDescription() {
    this(null, null, null, null, null, null, null);
  }

  public static DatabaseObjectDescription of(final DatabaseObject databaseObject) {
    if (databaseObject == null) {
      return new DatabaseObjectDescription();
    }
    final String simpleTypeName = MetaDataUtility.getSimpleTypeName(databaseObject).toString();
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
        simpleTypeName, completeType, name, fullName, description, intro, resource);
  }
}
