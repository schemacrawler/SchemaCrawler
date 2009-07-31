/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
package schemacrawler.utility;


import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ObjectToString
{

  private static final Logger LOGGER = Logger.getLogger(ObjectToString.class
    .getName());

  public static final String toString(final Object object)
  {
    if (object == null)
    {
      return "null";
    }

    final StringBuilder buffer = new StringBuilder();
    appendObject(object, 0, buffer);
    return buffer.toString();
  }

  public static final String toString(final Object object,
                                      final Map<String, Object> fields)
  {
    if (object == null)
    {
      return "null";
    }

    final StringBuilder buffer = new StringBuilder();
    appendHeader(object, 0, buffer);
    if (fields != null && !fields.isEmpty())
    {
      for (final Entry<String, Object> field: fields.entrySet())
      {
        buffer.append("  ").append(field.getKey()).append(": ");
        appendObject(field.getValue(), 1, buffer);
        buffer.append("\n");
      }
    }
    appendFooter(0, buffer);

    return buffer.toString();
  }

  /**
   * Appends the fields and values defined by the given object of the
   * given class.
   * 
   * @param clazz
   *        The class of object parameter
   */
  private static final void appendFields(final Object object,
                                         final int level,
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
          else if (Map.class.isAssignableFrom(fieldType))
          {
            final StringBuilder mapBuffer = new StringBuilder();
            for (Map.Entry<?, ?> mapEntry: ((Map<?, ?>) ((Map<?, ?>) fieldValue)).entrySet())
            {
              mapBuffer.append("\n").append(indent(level + 2)).append(mapEntry
                .getKey()).append(": ").append(mapEntry.getValue());
            }
            fieldValue = mapBuffer.toString();
          }
        }

        buffer.append(indent(level)).append("  ").append(fieldName)
          .append(": ");
        if (fieldType.isPrimitive() || fieldType.isEnum()
            || fieldValue instanceof String || fieldValue == null
            || definesToString(fieldValue))
        {
          buffer.append(fieldValue);
        }
        else
        {
          appendObject(fieldValue, level + 1, buffer);
        }
        buffer.append("\n");
      }
      catch (final Exception e)
      {
        LOGGER.log(Level.FINER, "Could not access field, " + field, e);
      }
    }
  }

  private static final void appendFooter(final int level,
                                         final StringBuilder buffer)
  {
    buffer.append(indent(level)).append("]");
  }

  private static final void appendHeader(final Object object,
                                         final int level,
                                         final StringBuilder buffer)
  {
    if (object != null)
    {
      buffer.append(indent(level)).append(object.getClass().getName())
        .append('@').append(Integer
          .toHexString(System.identityHashCode(object))).append("[\n");
    }
  }

  private static final void appendObject(final Object object,
                                         final int level,
                                         final StringBuilder buffer)
  {
    appendHeader(object, level, buffer);
    appendFields(object, level, buffer);
    appendFooter(level, buffer);
  }

  private static final boolean definesToString(final Object object)
  {
    boolean definesToString = false;
    final Class<?>[] classes = getClassHierarchy(object);
    if (classes.length > 0)
    {
      for (final Class<?> clazz: classes)
      {
        try
        {
          definesToString = clazz.getDeclaredMethod("toString", new Class[0]) != null;
          if (definesToString)
          {
            break;
          }
        }
        catch (final SecurityException e)
        {
          continue;
        }
        catch (final NoSuchMethodException e)
        {
          continue;
        }
      }
    }
    return definesToString;
  }

  private static final Class<?>[] getClassHierarchy(final Object object)
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

  private static final Field[] getFields(final Object object)
  {
    final Class<?>[] classes = getClassHierarchy(object);
    final List<Field> allFields = new ArrayList<Field>();
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
    Collections.sort(allFields, new Comparator<Field>()
    {

      public int compare(final Field field1, final Field field2)
      {
        return field1.getName().compareTo(field2.getName());
      }
    });

    return allFields.toArray(new Field[allFields.size()]);
  }

  private static final char[] indent(final int level)
  {
    if (level >= 0)
    {
      final char[] indentChars = new char[level * 2];
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
