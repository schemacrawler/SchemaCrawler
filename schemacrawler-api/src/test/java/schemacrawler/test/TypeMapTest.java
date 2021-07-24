/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/
package schemacrawler.test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import schemacrawler.utility.TypeMap;

public class TypeMapTest {

  @Test
  public void typeMap() throws Exception {

    final Map<String, Class<?>> map = new HashMap<>();
    map.put("string", String.class);
    map.put("object", Object.class);

    final TypeMap typeMap = new TypeMap(map);

    assertThrows(UnsupportedOperationException.class, () -> typeMap.clear());
    assertThrows(UnsupportedOperationException.class, () -> typeMap.put("long", Long.class));
    assertThrows(UnsupportedOperationException.class, () -> typeMap.putAll(map));
    assertThrows(UnsupportedOperationException.class, () -> typeMap.remove("int"));

    assertThat(typeMap.get("string"), is(String.class));
    assertThat(typeMap.get("long"), is(Object.class));

    assertThat(typeMap.toString(), is("{string=java.lang.String, object=java.lang.Object}"));
    assertThat(typeMap.size(), is(2));
    assertThat(typeMap.hashCode(), is(map.hashCode()));

    assertThat(new TypeMap((Map) null).isEmpty(), is(true));
    assertThat(new TypeMap(new HashMap<>()).isEmpty(), is(true));
  }
}
