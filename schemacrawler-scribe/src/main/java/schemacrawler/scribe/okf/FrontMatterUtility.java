/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf;

import java.net.URI;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.TypedObject;
import schemacrawler.scribe.okf.frontmatter.DatabaseObjectDescription;
import schemacrawler.scribe.okf.frontmatter.okf.Actor;
import schemacrawler.scribe.okf.frontmatter.okf.SchemaCrawlerActor;
import schemacrawler.scribe.okf.frontmatter.okf.TrustTier;
import schemacrawler.scribe.okf.frontmatter.okf.Verified;
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

  public static Verified verified() {
    final Actor scActor = new SchemaCrawlerActor().toActor();
    return new Verified(TrustTier.machine_confirmed, scActor);
  }

  private FrontMatterUtility() {
    // Prevent instantiation
  }
}
