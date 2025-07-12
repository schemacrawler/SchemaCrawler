/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */


package schemacrawler.schema;

public interface Reducible {

  <N extends NamedObject> void reduce(Class<N> clazz, Reducer<N> reducer);

  <N extends NamedObject> void undo(Class<N> clazz, Reducer<N> reducer);
}
