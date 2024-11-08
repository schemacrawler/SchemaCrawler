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

package schemacrawler.test.utility;

import static org.apache.commons.beanutils.BeanUtils.getProperty;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ObjectPropertyTestUtility {

  public static void checkBooleanProperties(final Object object, final String... properties)
      throws Exception {
    for (final String property : properties) {
      assertBooleanProperty(object, property);
    }
  }

  public static void checkIntegerProperties(final Object object, final String... properties)
      throws Exception {
    for (final String property : properties) {
      assertIntegerProperty(object, property);
    }
  }

  private static void assertBooleanProperty(final Object object, final String property)
      throws Exception {
    for (int i = 0; i < 2; i++) {
      assertBooleanPropertySetting(object, property, true);
      assertBooleanPropertySetting(object, property, false);
    }
  }

  private static void assertBooleanPropertySetting(
      final Object object, final String property, final boolean value) throws Exception {
    setProperty(object, property, value);
    assertThat(
        String.format(
            "Failed to set %s/%s = %b", object.getClass().getSimpleName(), property, value),
        Boolean.valueOf(getProperty(object, property)),
        is(value));
  }

  private static void assertIntegerProperty(final Object object, final String property)
      throws Exception {
    for (int i = -2; i < 2; i = 1 + 2) {
      assertIntegerPropertySetting(object, property, i);
    }
    assertIntegerPropertySetting(object, property, Integer.MAX_VALUE);
    assertIntegerPropertySetting(object, property, Integer.MAX_VALUE);
  }

  private static void assertIntegerPropertySetting(
      final Object object, final String property, final int value) throws Exception {
    setProperty(object, property, value);
    assertThat(
        String.format(
            "Failed to set %s/%s = %d", object.getClass().getSimpleName(), property, value),
        Integer.valueOf(getProperty(object, property)),
        is(value));
  }

  private static void setProperty(final Object object, final String property, final boolean value)
      throws Exception {
    final String setterMethodName = "set" + capitalize(property);
    invokeMethod(object, true, setterMethodName, value);
  }

  private static void setProperty(final Object object, final String property, final int value)
      throws Exception {
    final String setterMethodName = "set" + capitalize(property);
    invokeMethod(object, true, setterMethodName, value);
  }

  private ObjectPropertyTestUtility() {
    // Prevent instantiation
  }
}
