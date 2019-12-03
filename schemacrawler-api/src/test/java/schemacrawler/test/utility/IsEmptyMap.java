/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.utility;


import java.util.Map;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class IsEmptyMap<K, V>
  extends TypeSafeMatcher<Map<K, V>>
{

  /**
   * Creates a matcher for {@link java.util.Map}s matching examined
   * collections whose <code>isEmpty</code> method returns
   * <code>true</code>.
   * <p/>
   * For example:
   *
   * <pre>
   * assertThat(new HashMap&lt;String, String&gt;(), is(empty()))
   * </pre>
   */
  public static <K, V> Matcher<Map<K, V>> emptyMap()
  {
    return new IsEmptyMap<K, V>();
  }

  @Override
  public void describeTo(final Description description)
  {
    description.appendText("an empty map");
  }

  @Override
  protected boolean matchesSafely(final Map<K, V> item)
  {
    return item.isEmpty();
  }

}
