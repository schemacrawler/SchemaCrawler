/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.loader.attributes.model;

import java.beans.ConstructorProperties;
import java.io.Serial;
import java.util.List;
import java.util.Map;

public class ColumnAttributes extends ObjectAttributes {

  @Serial private static final long serialVersionUID = -7531479565539199840L;

  @ConstructorProperties({"name", "remarks", "attributes"})
  public ColumnAttributes(
      final String name, final List<String> remarks, final Map<String, String> attributes) {
    super(name, remarks, attributes);
  }
}
