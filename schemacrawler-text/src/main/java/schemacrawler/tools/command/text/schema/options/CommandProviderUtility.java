/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.command.text.schema.options;

import java.util.ArrayList;
import java.util.Collection;
import us.fatehi.utility.property.PropertyName;

public class CommandProviderUtility {

  public static Collection<PropertyName> schemaTextCommands() {
    final Collection<PropertyName> supportedCommands = new ArrayList<>();
    for (final SchemaTextDetailType schemaTextDetailType : SchemaTextDetailType.values()) {
      supportedCommands.add(schemaTextDetailType.toPropertyName());
    }
    return supportedCommands;
  }

  private CommandProviderUtility() {
    // Prevent instantiation
  }
}
