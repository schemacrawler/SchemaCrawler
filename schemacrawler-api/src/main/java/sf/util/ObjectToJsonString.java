/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2011, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package sf.util;


import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ObjectToJsonString
{

  private static final Logger LOGGER = Logger
    .getLogger(ObjectToJsonString.class.getName());

  private static void appendFields(final Object object,
                                   final int indent,
                                   final StringBuilder buffer)
  {
    if (object == null)
    {
      return;
    }
    for (final Method method: getReadMethods(object))
    {
      try
      {
        final Class<?> fieldType = method.getReturnType();
        final String fieldName = method.getName();

        Object fieldValue = method.invoke(object);
        if (fieldValue != null)
        {
          if (fieldType.isArray())
          {
            fieldValue = Arrays.toString((Object[]) fieldValue);
          }
        }

        buffer.append(indent(indent)).append("  \"").append(fieldName)
          .append("\": ");
        if (fieldType.isPrimitive() || fieldType.isEnum()
            || fieldValue instanceof String || fieldValue == null)
        {
          buffer.append("\"").append(fieldValue).append("\"");
        }
        else
        {
          appendObject(fieldValue, indent + 1, buffer);
        }
        buffer.append(Utility.NEWLINE);
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.FINER, "Could not invoke, " + method, e);
      }
    }
  }

  private static void appendFooter(final int indent, final StringBuilder buffer)
  {
    buffer.append(indent(indent)).append(']');
  }

  private static void appendHeader(final Object object,
                                   final int indent,
                                   final StringBuilder buffer)
  {
    if (object != null)
    {
      buffer.append(indent(indent)).append(object.getClass().getName())
        .append('@')
        .append(Integer.toHexString(System.identityHashCode(object)))
        .append('[').append(Utility.NEWLINE);
    }
  }

  private static void appendObject(final Object object,
                                   final int indent,
                                   final StringBuilder buffer)
  {
    final Class<?> objectClass = object.getClass();
    if (Map.class.isAssignableFrom(objectClass))
    {
      final Set<Map.Entry> mapEntries = new TreeMap((Map) object).entrySet();
      for (final Map.Entry mapEntry: mapEntries)
      {
        buffer.append(Utility.NEWLINE).append(indent(indent))
          .append(mapEntry.getKey()).append(": ").append(mapEntry.getValue());
      }
    }
    else if (Collection.class.isAssignableFrom(objectClass))
    {
      for (final Iterator<?> iterator = ((Collection<?>) object).iterator(); iterator
        .hasNext();)
      {
        final Object item = iterator.next();
        buffer.append(item);
        if (iterator.hasNext())
        {
          buffer.append(", ");
        }
      }
    }
    else if (objectClass.isArray())
    {
      for (final Iterator<?> iterator = Arrays.asList((Object[]) object)
        .iterator(); iterator.hasNext();)
      {
        final Object item = iterator.next();
        buffer.append(item);
        if (iterator.hasNext())
        {
          buffer.append(", ");
        }
      }
    }
    else if (Arrays.asList(Integer.class,
                           Long.class,
                           Double.class,
                           Float.class,
                           Boolean.class,
                           Character.class,
                           Byte.class,
                           Void.class,
                           Short.class,
                           String.class).contains(objectClass))
    {
      buffer.append(object.toString());
    }
    else
    {
      appendHeader(object, 0, buffer);
      appendFields(object, indent, buffer);
      appendFooter(indent, buffer);
    }
  }

  private static Class<?>[] getClassHierarchy(final Object object)
  {
    final List<Class<?>> classHierarchy = new ArrayList<Class<?>>();
    if (object != null)
    {
      Class<?> clazz = object.getClass();
      classHierarchy.add(clazz);
      while (clazz.getSuperclass() != null)
      {
        clazz = clazz.getSuperclass();
        if (clazz.getSuperclass() != null)
        {
          classHierarchy.add(clazz);
        }
      }
    }
    return classHierarchy.toArray(new Class<?>[classHierarchy.size()]);
  }

  private static Method[] getReadMethods(final Object object)
  {
    final List<Method> readMethodsList = new ArrayList<Method>();
    try
    {
      final BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
      final PropertyDescriptor[] propertyDescriptors = beanInfo
        .getPropertyDescriptors();
      for (final PropertyDescriptor propertyDescriptor: propertyDescriptors)
      {
        final Method readMethod = propertyDescriptor.getReadMethod();

        readMethodsList.add(readMethod);
      }
    }
    catch (final IntrospectionException e2)
    {
      e2.printStackTrace();
    }

    final Method[] readMethods = readMethodsList
      .toArray(new Method[readMethodsList.size()]);
    AccessibleObject.setAccessible(readMethods, true);
    Arrays.sort(readMethods);

    return readMethods;
  }

  private static char[] indent(final int indent)
  {
    if (indent >= 0)
    {
      final char[] indentChars = new char[indent * 2];
      Arrays.fill(indentChars, ' ');
      return indentChars;
    }
    else
    {
      return new char[0];
    }
  }

  public static String toString(final Object object)
  {
    if (object == null)
    {
      return "null";
    }
    final int indent = 0;

    final StringBuilder buffer = new StringBuilder();
    appendObject(object, indent, buffer);
    return buffer.toString();
  }

  private ObjectToJsonString()
  {
  }

}
