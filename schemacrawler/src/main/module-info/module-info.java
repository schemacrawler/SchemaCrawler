/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

module us.fatehi.schemacrawler.schemacrawler {
  // Required modules
  requires java.sql;
  requires java.logging;

  // Export public API packages from schemacrawler-utility
  exports us.fatehi.utility.database;
  exports us.fatehi.utility.datasource;
  exports us.fatehi.utility.graph;
  exports us.fatehi.utility.html;
  exports us.fatehi.utility.ioresource;
  exports us.fatehi.utility.property;
  exports us.fatehi.utility.readconfig;
  exports us.fatehi.utility.scheduler;
  exports us.fatehi.utility.string;

  // Export public API packages from schemacrawler-api
  exports schemacrawler.schema;
  exports schemacrawler.schemacrawler;
  exports schemacrawler.schemacrawler.exceptions;
  exports schemacrawler.filter;
  exports schemacrawler.inclusionrule;
  exports schemacrawler.plugin;
  exports schemacrawler.utility;
  // Do NOT export schemacrawler.crawl - this is an internal implementation package

  // Export public API packages from schemacrawler-tools
  exports schemacrawler.tools.catalogloader;
  exports schemacrawler.tools.databaseconnector;
  exports schemacrawler.tools.executable;
  exports schemacrawler.tools.executable.commandline;
  exports schemacrawler.tools.options;
  exports schemacrawler.tools.registry;
  exports schemacrawler.tools.utility;

  // Export public API packages from schemacrawler-loader
  exports schemacrawler.loader.attributes;
  exports schemacrawler.loader.attributes.model;
  exports schemacrawler.loader.counts;
  exports schemacrawler.loader.weakassociations;

  // Export public API packages from schemacrawler-text
  exports schemacrawler.tools.command.text.schema;
  exports schemacrawler.tools.command.text.schema.options;
  exports schemacrawler.tools.text.formatter.base;
  exports schemacrawler.tools.text.formatter.base.helper;
  exports schemacrawler.tools.text.formatter.schema;
  exports schemacrawler.tools.text.options;
  exports schemacrawler.tools.traversal;

  // Export public API packages from schemacrawler-diagram
  exports schemacrawler.tools.command.text.diagram;
  exports schemacrawler.tools.command.text.diagram.options;
  exports schemacrawler.tools.command.text.embeddeddiagram;
  exports schemacrawler.tools.text.formatter.diagram;

  // ServiceLoader providers
  uses schemacrawler.tools.catalogloader.CatalogLoader;
  uses schemacrawler.tools.executable.CommandProvider;
}
