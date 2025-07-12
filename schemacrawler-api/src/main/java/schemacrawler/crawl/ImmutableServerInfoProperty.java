/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.crawl;

import us.fatehi.utility.property.AbstractProperty;
import us.fatehi.utility.property.PropertyName;

final class ImmutableServerInfoProperty extends AbstractProperty {

  private static final long serialVersionUID = -2744384718272515235L;

  ImmutableServerInfoProperty(final String name, final String value, final String description) {
    super(new PropertyName(name, description), value);
  }
}
