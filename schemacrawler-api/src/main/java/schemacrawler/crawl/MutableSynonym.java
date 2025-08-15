/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.crawl;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collection;
import schemacrawler.schema.DatabaseObject;
import schemacrawler.schema.Schema;
import schemacrawler.schema.Synonym;

/**
 * Represents a database synonym. Created from metadata returned by a JDBC call.
 *
 * <p>(Based on an idea from Matt Albrecht)
 */
final class MutableSynonym extends AbstractDatabaseObject implements Synonym {

  private static final long serialVersionUID = -5980593047288755771L;

  private DatabaseObject referencedObject;

  MutableSynonym(final Schema schema, final String name) {
    super(schema, name);
  }

  @Override
  public DatabaseObject getReferencedObject() {
    return referencedObject;
  }

  @Override
  public Collection<? extends DatabaseObject> getReferencedObjects() {
    return Arrays.asList(referencedObject);
  }

  void setReferencedObject(final DatabaseObject referencedObject) {
    this.referencedObject = requireNonNull(referencedObject, "Referenced object not provided");
  }
}
