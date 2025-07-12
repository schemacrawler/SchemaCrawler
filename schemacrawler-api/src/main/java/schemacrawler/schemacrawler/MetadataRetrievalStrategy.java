/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schemacrawler;

public enum MetadataRetrievalStrategy {
  // Do not load metadata
  none,
  // use JDBC metadata calls to retrieve information one database object at a time;
  // each JDBC driver decides how to honor these requests
  metadata,
  // use the INFORMATION_SCHEMA or database-specific data dictionary queries to
  // retrieve information for all database objects together
  data_dictionary_all
}
