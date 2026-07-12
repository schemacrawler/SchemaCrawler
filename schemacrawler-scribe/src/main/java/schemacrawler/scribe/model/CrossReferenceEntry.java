/*
 * SchemaCrawler Scribe
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.scribe.model;

import schemacrawler.schema.DatabaseObject;
import schemacrawler.utility.MetaDataUtility.SimpleDatabaseObjectType;

/** One object-to-object "used by" cross-reference row for template rendering. */
public record CrossReferenceEntry(
    DatabaseObject databaseObject,
    SimpleDatabaseObjectType databaseObjectType,
    DatabaseObject usedByDatabaseObject,
    SimpleDatabaseObjectType usedByDatabaseObjectType) {}
