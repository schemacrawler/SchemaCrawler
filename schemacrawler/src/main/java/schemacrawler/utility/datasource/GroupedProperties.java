/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.utility.datasource;


import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * Partitions properties by grouping properties with a given prefix. The
 * prefix is removed in the result.
 * 
 * @author <a
 *         href='mailto:sualeh85@yahoo.com?subject=QuestionBank'>Sualeh
 *         Fatehi</a>
 * @version Version 0.1
 */
final class GroupedProperties
  extends Properties
{

  private static final long serialVersionUID = 3544393595470493235L;

  /**
   * Constructs a new group from the given properties.
   */
  GroupedProperties()
  {
    super();
  }

  /**
   * Constructs a new group from the given properties.
   * 
   * @param properties
   *        Base properties properties
   */
  GroupedProperties(final Properties properties)
  {
    final Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();
    for (final Map.Entry<Object, Object> entry: entrySet)
    {
      final String key = (String) entry.getKey();
      final String value = (String) entry.getValue();
      setProperty(key, value);
    }
  }

  /**
   * Gets available groups (dot-delimited keys) in the properties.
   * 
   * @return List of unique group names.
   */
  String[] groups()
  {
    final Set<String> groups = new TreeSet<String>();

    final Set<Object> keys = keySet();
    for (final Object object: keys)
    {
      final String key = (String) object;
      if (key.indexOf('.') > -1)
      {
        final String group = key.substring(0, key.indexOf('.'));
        groups.add(group);
      }
    }

    return groups.toArray(new String[groups.size()]);
  }

  /**
   * Checks if the provided group name is a valid group.
   * 
   * @param prefix
   *        Group name
   * @return True if the group is valid
   */
  boolean isGroup(final String prefix)
  {
    boolean foundGroup = false;

    if (prefix.length() > 0)
    {
      final String[] groups = groups();

      for (final String group: groups)
      {
        if (group.equals(prefix))
        {
          foundGroup = true;

          break;
        }
      }
    }

    return foundGroup;
  }

  /**
   * Gets a sub-group of properties - those that start with a given
   * prefix. The prefix is removed in the result.
   * 
   * @param prefix
   *        Prefix to group by.
   * @return Group of properties.
   */
  Properties subgroup(final String prefix)
  {
    if (prefix == null || prefix.length() == 0)
    {
      return this;
    }

    final String dottedPrefix = prefix + ".";
    final Properties subgroup = new Properties();

    final Set<Object> keys = keySet();
    for (final Object object: keys)
    {
      final String key = (String) object;
      if (key.startsWith(dottedPrefix))
      {
        final String unprefixed = key.substring(dottedPrefix.length());
        subgroup.setProperty(unprefixed, getProperty(key));
      }
    }

    return subgroup;
  }
}
