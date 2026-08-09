/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import schemacrawler.schemacrawler.Version;
import us.fatehi.utility.property.ProductVersion;

public final class SchemaCrawlerActor implements OkfActor {

  private final ActorType actorType;
  private final String actor;

  public SchemaCrawlerActor() {
    actorType = ActorType.process;

    final ProductVersion version = Version.version();
    final String actorName = version.getProductName().toLowerCase().replaceAll("[^a-z0-9]+", "");
    actor = "%s-v%s".formatted(actorName, version.getProductVersion());
  }

  @Override
  public String getActor() {
    return actor;
  }

  @Override
  public ActorType getActorType() {
    return actorType;
  }

  @Override
  public String toString() {
    return "%s:%s".formatted(actorType, actor);
  }
}
