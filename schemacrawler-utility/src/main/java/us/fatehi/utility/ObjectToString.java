/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

import static java.lang.System.lineSeparator;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;

public class ObjectToString {

  public static List<?> arrayToList(final Object array) {
    if (array == null || !array.getClass().isArray()) {
      return null;
    }

    final int length = Array.getLength(array);
    final List<Object> objectList = new ArrayList<>();
    for (int i = 0; i < length; i++) {
      objectList.add(Array.get(array, i));
    }
    return objectList;
  }

  public static List<Class<?>> classHierarchy(final Object object) {
    final List<Class<?>> classHierarchy = new ArrayList<>();
    if (object != null) {
      Class<?> clazz = object.getClass();
      classHierarchy.add(clazz);
      while (clazz.getSuperclass() != null) {
        clazz = clazz.getSuperclass();
        if (clazz.getSuperclass() != null) {
          classHierarchy.add(clazz);
        }
      }
    }
    return classHierarchy;
  }

  public static List<?> collectionOrArrayToList(final Object object) {

    if (!isCollectionOrArray(object)) {
      return new ArrayList<>();
    }

    if (object instanceof List) {
      return (List<?>) object;
    }
    if (object instanceof Collection) {
      return new ArrayList<>((Collection<?>) object);
    }
    // We have checked earlier if this was an array, so at this point we are pretty sure it is an
    // array
    return arrayToList(object);
  }

  public static List<Field> fields(final Object object) {
    final List<Class<?>> classes = classHierarchy(object);
    final List<Field> allFields = new ArrayList<>();
    for (final Class<?> clazz : classes) {
      if (clazz.isArray()
          || clazz.isPrimitive()
          || clazz.isEnum()
          || String.class.isAssignableFrom(clazz)) {
        break;
      }
      Field[] fields = {};
      try {
        fields = clazz.getDeclaredFields();
        AccessibleObject.setAccessible(fields, true);
      } catch (final Exception e) {
        // Ignore
      }
      allFields.addAll(Arrays.asList(fields));
    }

    // Remove static and transient fields
    for (final Iterator<Field> iterator = allFields.iterator(); iterator.hasNext(); ) {
      final Field field = iterator.next();
      final int modifiers = field.getModifiers();
      if (Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers)) {
        iterator.remove();
      }
    }
    // Sort fields
    Collections.sort(allFields, Comparator.comparing(Field::getName));

    return allFields;
  }

  public static boolean isCollectionOrArray(final Object object) {

    if (object == null) {
      return false;
    }

    final Class<?> objectClass = object.getClass();
    if (Collection.class.isAssignableFrom(objectClass) || objectClass.isArray()) {
      return true;
    }
    return false;
  }

  public static boolean isPrimitive(final Object object) {

    if (object == null) {
      return false;
    }

    final Class<?> objectClass = object.getClass();
    return Arrays.asList(
            Integer.class,
            Long.class,
            Double.class,
            Float.class,
            Boolean.class,
            Byte.class,
            Void.class,
            Short.class)
        .contains(objectClass);
  }

  public static boolean isSimpleObject(final Object object) {

    if (object == null) {
      return false;
    }

    final Class<?> objectClass = object.getClass();
    return isPrimitive(object)
        || object instanceof String
        || object instanceof Character
        || objectClass.isEnum();
  }

  public static String listOrObjectToString(final Object object) {
    if (isCollectionOrArray(object)) {
      final List<?> list = collectionOrArrayToList(object);
      final StringJoiner listJoiner = new StringJoiner(", ");
      for (final Object element : list) {
        listJoiner.add(String.valueOf(element));
      }
      return listJoiner.toString();
    }

    return String.valueOf(object);
  }

  public static Map<String, Object> objectMap(final Object object) {

    final Map<String, Object> objectMap = new TreeMap<>();
    if (object == null || isCollectionOrArray(object) || isSimpleObject(object)) {
      return objectMap;
    }

    final Class<?> objectClass = object.getClass();
    if (Map.class.isAssignableFrom(objectClass)) {
      final Set<Map.Entry<?, ?>> mapEntries = ((Map) object).entrySet();
      for (final Map.Entry<?, ?> mapEntry : mapEntries) {
        objectMap.put(String.valueOf(mapEntry.getKey()), mapEntry.getValue());
      }
    } else {
      objectMap.put("@object", object.getClass().getName());
      // objectMap.put("@hash", Integer.toHexString(System.identityHashCode(object)));
      for (final Field field : fields(object)) {
        try {
          Object value = field.get(object);
          if (isCollectionOrArray(value)) {
            value = collectionOrArrayToList(value);
          }
          objectMap.put(field.getName(), value);
        } catch (final Exception e) {
          // Ignore
        }
      }
    }

    return objectMap;
  }

  public static String toString(final Object object) {
    if (object == null || isSimpleObject(object)) {
      return String.valueOf(object);
    }
    if (isCollectionOrArray(object)) {
      return printList(collectionOrArrayToList(object));
    }

    return printMap(0, objectMap(object));
  }

  private static char[] indent(final int indent) {
    // assert indent >= 0;

    final char[] indentChars = new char[indent * 2];
    Arrays.fill(indentChars, ' ');
    return indentChars;
  }

  private static String printList(final List<?> list) {
    // assert list != null;

    final StringBuilder buffer = new StringBuilder();
    buffer.append('[');
    for (final Iterator<?> iterator = list.iterator(); iterator.hasNext(); ) {
      final Object object = iterator.next();
      if (isPrimitive(object)) {
        buffer.append(String.valueOf(object));
      } else {
        buffer.append('\"').append(String.valueOf(object)).append('\"');
      }
      if (iterator.hasNext()) {
        buffer.append(", ");
      }
    }
    buffer.append(']');

    return buffer.toString();
  }

  private static String printMap(final int indent, final Map<String, Object> objectMap) {
    // assert objectMap != null;

    TreeMap<String, Object> map = new TreeMap<>(objectMap);

    final StringBuilder buffer = new StringBuilder();

    buffer.append(indent(indent)).append('{').append(lineSeparator());
    final Set<Entry<String, Object>> entrySet = map.entrySet();
    for (final Iterator<Entry<String, Object>> iterator = entrySet.iterator();
        iterator.hasNext(); ) {
      final Entry<String, Object> entry = iterator.next();
      Object value = entry.getValue();
      if (value != null) {
        final Class<? extends Object> valueClass = value.getClass();
        if (List.class.isAssignableFrom(valueClass)) {
          value = printList((List<?>) value);
        } else if (Map.class.isAssignableFrom(valueClass)) {
          value = printMap(indent + 1, objectMap(value));
        } else if (!isPrimitive(value) || value instanceof String || valueClass.isEnum()) {
          value = String.format("\"%s\"", value);
        }
      }
      buffer
          .append(indent(indent + 1))
          .append("\"")
          .append(entry.getKey())
          .append("\": ")
          .append(String.valueOf(value));
      if (iterator.hasNext()) {
        buffer.append(",");
      }
      buffer.append(lineSeparator());
    }
    buffer.append(indent(indent)).append('}');

    return buffer.toString();
  }

  private ObjectToString() {
    // Prevent instantiation
  }
}
