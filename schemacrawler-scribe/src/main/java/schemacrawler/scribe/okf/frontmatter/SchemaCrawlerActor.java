/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */
package schemacrawler.scribe.okf.frontmatter;

import schemacrawler.schemacrawler.Version;
import us.fatehi.utility.UtilityMarker;
import us.fatehi.utility.property.ProductVersion;

@UtilityMarker
public class SchemaCrawlerActor {

  public static String schemaCrawlerActor() {
    final ProductVersion version = Version.version();
    final String actorName = version.getProductName().toLowerCase().replaceAll("[^a-z0-9]+", "");
    final String actorValue = "%s-v%s".formatted(actorName, version.getProductVersion());
    return actorValue;
  }

  private SchemaCrawlerActor() {
    // Prevent instantiation
  }
}
