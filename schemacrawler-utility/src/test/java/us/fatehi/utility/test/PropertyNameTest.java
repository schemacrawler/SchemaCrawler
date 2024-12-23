/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import nl.jqno.equalsverifier.EqualsVerifier;
import us.fatehi.utility.property.PropertyName;

public class PropertyNameTest {

  @Test
  public void propertyName() {
    EqualsVerifier.forClass(PropertyName.class).withIgnoredFields("description").verify();
  }

  @Test
  public void compare() {
    final PropertyName propertyName1 = new PropertyName("hello1", "world");
    final PropertyName propertyName2 = new PropertyName("hello", "  ");
    assertThat(propertyName1.compareTo(propertyName2), is(equalTo(1)));

    assertThat(propertyName1.compareTo(null), is(equalTo(-1)));
  }

  @Test
  public void testString() {
    final PropertyName propertyName1 = new PropertyName("hello", "world");
    assertThat(propertyName1.getName(), is(equalTo("hello")));
    assertThat(propertyName1.getDescription(), is(equalTo("world")));
    assertThat(propertyName1.toString(), is(equalTo("hello - world")));

    final PropertyName propertyName2 = new PropertyName("hello", "  ");
    assertThat(propertyName2.getName(), is(equalTo("hello")));
    assertThat(propertyName2.getDescription(), is(equalTo("")));
    assertThat(propertyName2.toString(), is(equalTo("hello")));
  }
}
