/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

public interface Reducer<N extends NamedObject> {

  void reduce(final ReducibleCollection<? extends N> namedObjects);

  void undo(final ReducibleCollection<? extends N> namedObjects);
}
