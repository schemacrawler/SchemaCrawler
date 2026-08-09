/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.okf;

import schemacrawler.schemacrawler.Version;
import schemacrawler.scribe.okf.frontmatter.okf.Actor.ActorType;
import us.fatehi.utility.property.ProductVersion;

public final class SchemaCrawlerActor {

  private final ActorType actorType;
  private final String actor;

  public SchemaCrawlerActor() {
    actorType = ActorType.process;

    final ProductVersion version = Version.version();
    final String actorName = version.getProductName().toLowerCase().replaceAll("[^a-z0-9]+", "");
    actor = "%s-v%s".formatted(actorName, version.getProductVersion());
  }

  public String actor() {
    return actor;
  }

  public ActorType actorType() {
    return actorType;
  }

  public Actor toActor() {
    return new Actor(actorType, actor);
  }

  @Override
  public String toString() {
    return "%s:%s".formatted(actorType, actor);
  }
}
