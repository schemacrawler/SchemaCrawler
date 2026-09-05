/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.importance.model;

/** Categorizes a directed dependency in the schema graph. */
public enum EdgeType {
  FOREIGN_KEY(1.00),
  VIEW_DEPENDENCY(0.80),
  ROUTINE_DEPENDENCY(0.70),
  IMPLICIT_ASSOCIATION(0.50),
  SYNONYM_RESOLUTION(0.20);

  private final double weight;

  EdgeType(final double weight) {
    this.weight = weight;
  }

  public double getWeight() {
    return weight;
  }
}
