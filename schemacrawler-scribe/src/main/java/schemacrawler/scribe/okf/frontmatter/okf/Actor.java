/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter.okf;

public interface Actor {

  public enum ActorType {
    human,
    process;
  }

  String getActor();

  ActorType getActorType();
}
