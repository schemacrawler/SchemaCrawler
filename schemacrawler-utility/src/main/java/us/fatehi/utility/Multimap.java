/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Multimap<K, V> extends HashMap<K, List<V>> {

  @Serial private static final long serialVersionUID = 1470713639458689002L;

  public V add(final K key, final V value) {
    List<V> values = null;
    if (containsKey(key)) {
      values = get(key);
    }
    if (values == null) {
      values = new ArrayList<>();
    }
    put(key, values);

    values.add(value);
    return value;
  }
}
