/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.okf;

import static us.fatehi.utility.Utility.requireNotBlank;

public record Actor(ActorType actorType, String actor) {

  public enum ActorType {
    agent,
    human,
    process;
  }

  public Actor {
    actorType = actorType == null ? actorType = ActorType.process : actorType;
    requireNotBlank(actor, "No actor provided");
  }

  @Override
  public String toString() {
    if (actorType == ActorType.process) {
      return actor;
    }
    return "%s:%s".formatted(actorType, actor);
  }
}
