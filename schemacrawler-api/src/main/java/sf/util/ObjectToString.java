/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ObjectToString
{

  private static final Logger LOGGER = Logger
    .getLogger(ObjectToString.class.getName());

  public static String toString(final Object object)
  {
    if (object == null)
    {
      return "null";
    }
    final int indent = 0;

    final StringBuilder buffer = new StringBuilder(1024);
    appendObject(object, indent, buffer);
    return buffer.toString();
  }

  private static void appendFields(final Object object,
                                   final int indent,
                                   final StringBuilder buffer)
  {
    if (object == null)
    {
      return;
    }
    for (final Field field: getFields(object))
    {
      try
      {
        final String fieldName = field.getName();
        Object fieldValue = field.get(object);
        final Class<?> fieldType = field.getType();

        if (fieldValue != null)
        {
          if (fieldType.isArray())
          {
            fieldValue = Arrays.toString((Object[]) fieldValue);
          }
        }

        buffer.append(indent(indent)).append("  ").append(fieldName)
          .append(": ");
        if (fieldType.isPrimitive() || fieldType.isEnum()
            || fieldValue instanceof String || fieldValue == null
            || definesToString(fieldValue))
        {
          buffer.append(fieldValue);
        }
        else
        {
          appendObject(fieldValue, indent + 1, buffer);
        }
        buffer.append(System.lineSeparator());
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.FINER,
                   e,
                   new FormattedStringSupplier("Could not access field, %s",
                                               field));
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
        .append('[').append(System.lineSeparator());
    }
  }

  private static void appendIterable(final Iterator<?> iterator,
                                     final String delimiter,
                                     final StringBuilder buffer)
  {
    while (iterator.hasNext())
    {
      final Object item = iterator.next();
      buffer.append(item);
      if (iterator.hasNext())
      {
        buffer.append(delimiter);
      }
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
        buffer.append(System.lineSeparator()).append(indent(indent))
          .append(mapEntry.getKey()).append(": ").append(mapEntry.getValue());
      }
    }
    else if (Collection.class.isAssignableFrom(objectClass))
    {
      appendIterable(((Collection<?>) object).iterator(), ", ", buffer);
    }
    else if (objectClass.isArray())
    {
      appendIterable(Arrays.asList((Object[]) object).iterator(), ", ", buffer);
    }
    else if (objectClass.isEnum())
    {
      buffer.append(object.toString());
    }
    else if (Arrays
      .asList(Integer.class,
              Long.class,
              Double.class,
              Float.class,
              Boolean.class,
              Character.class,
              Byte.class,
              Void.class,
              Short.class,
              String.class)
      .contains(objectClass))
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

  private static boolean definesToString(final Object object)
  {
    boolean definesToString = false;
    final Class<?>[] classes = getClassHierarchy(object);
    if (classes.length > 0)
    {
      for (final Class<?> clazz: classes)
      {
        try
        {
          definesToString = clazz.getDeclaredMethod("toString") != null;
          if (definesToString)
          {
            break;
          }
        }
        catch (final SecurityException | NoSuchMethodException e)
        {
          // continue
        }
      }
    }
    return definesToString;
  }

  private static Class<?>[] getClassHierarchy(final Object object)
  {
    final List<Class<?>> classHierarchy = new ArrayList<>();
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

  private static Field[] getFields(final Object object)
  {
    final Class<?>[] classes = getClassHierarchy(object);
    final List<Field> allFields = new ArrayList<>();
    if (classes != null && classes.length > 0)
    {
      for (final Class<?> clazz: classes)
      {
        if (clazz.isArray() || clazz.isPrimitive() || clazz.isEnum()
            || String.class.isAssignableFrom(clazz))
        {
          break;
        }
        final Field[] fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
        allFields.addAll(Arrays.asList(fields));
      }
    }
    // Remove static and transient fields
    for (final Iterator<Field> iterator = allFields.iterator(); iterator
      .hasNext();)
    {
      final Field field = iterator.next();
      final int modifiers = field.getModifiers();
      if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers)
          || Modifier.isVolatile(modifiers))
      {
        iterator.remove();
      }
    }
    // Sort fields
    Collections
      .sort(allFields,
            (field1, field2) -> field1.getName().compareTo(field2.getName()));

    return allFields.toArray(new Field[allFields.size()]);
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

  private ObjectToString()
  {
  }

}
