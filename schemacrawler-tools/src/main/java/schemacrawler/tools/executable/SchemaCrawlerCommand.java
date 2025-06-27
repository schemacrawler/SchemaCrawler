/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.tools.executable;

import schemacrawler.schemacrawler.Identifiers;
import schemacrawler.schemacrawler.InformationSchemaViews;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;
import schemacrawler.tools.options.OutputOptions;

/** A SchemaCrawler tools executable unit. */
public interface SchemaCrawlerCommand<C extends CommandOptions> extends Command<C, Void> {

  /**
   * Checks whether a command is available, and throws a runtime exception if it is not available.
   */
  void checkAvailability();

  /**
   * Executes functionality for SchemaCrawler, after database metadata has been obtained. May throw
   * runtime exceptions on errors.
   */
  void execute();

  C getCommandOptions();

  Identifiers getIdentifiers();

  InformationSchemaViews getInformationSchemaViews();

  OutputOptions getOutputOptions();

  SchemaCrawlerOptions getSchemaCrawlerOptions();

  void setIdentifiers(Identifiers identifiers);

  void setInformationSchemaViews(InformationSchemaViews informationSchemaViews);

  void setOutputOptions(OutputOptions outputOptions);

  void setSchemaCrawlerOptions(SchemaCrawlerOptions schemaCrawlerOptions);
}
