/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schema;

import static java.sql.DatabaseMetaData.typePredBasic;
import static java.sql.DatabaseMetaData.typePredChar;
import static java.sql.DatabaseMetaData.typePredNone;
import static java.sql.DatabaseMetaData.typeSearchable;

import us.fatehi.utility.IdentifiedEnum;

/** An enumeration wrapper around JDBC search predicates. */
public enum SearchableType implements IdentifiedEnum {
  unknown(-1, "unknown"),
  notSearchable(typePredNone, "not searchable"),
  searchableWithLike(typePredChar, "only searchable with where .. like"),
  searchableWithoutLike(typePredBasic, "searchable except with where .. like"),
  searchable(typeSearchable, "searchable");

  private final int id;
  private final String text;

  SearchableType(final int id, final String text) {
    this.id = id;
    this.text = text;
  }

  /** {@inheritDoc} */
  @Override
  public int id() {
    return id;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    if (this == unknown) {
      return "not searchable";
    } else {
      return text;
    }
  }
}
