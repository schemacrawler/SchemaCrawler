package schemacrawler.test.utility;


import static org.apache.commons.beanutils.BeanUtils.getProperty;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.reflect.MethodUtils.invokeMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.lang.reflect.InvocationTargetException;

public class BooleanPropertyTestUtility
{



  public static void checkBooleanProperties(final Object object, final String... properties)
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    for (final String property : properties)
    {
      assertBooleanProperty(object, property);
    }
  }

  private static void assertBooleanProperty(final Object object, final String property)
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    for (int i = 0; i < 2; i++)
    {
      assertBooleanPropertySetting(object, property, true);
      assertBooleanPropertySetting(object, property, false);
    }
  }

  private static void assertBooleanPropertySetting(final Object object, final String property, final boolean value)
    throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
  {
    setProperty(object, property, value);
    assertThat(String.format("Failed to set %s/%s = %b",
                             object
                               .getClass()
                               .getSimpleName(),
                             property,
                             value), Boolean.valueOf(getProperty(object, property)), is(value));
  }

  private static void setProperty(final Object object, final String property, final boolean value)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
  {
    final String setterMethodName = "set" + capitalize(property);
    invokeMethod(object, true, setterMethodName, value);
  }

  private BooleanPropertyTestUtility()
  {
    // Prevent instantiation
  }

}
