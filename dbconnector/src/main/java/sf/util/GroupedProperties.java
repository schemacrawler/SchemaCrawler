/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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
public final class GroupedProperties
  extends Properties
{

  private static final long serialVersionUID = 3544393595470493235L;

  /**
   * Constructs a new group from the given properties.
   */
  public GroupedProperties()
  {
    super();
  }

  /**
   * Constructs a new group from the given properties.
   * 
   * @param properties
   *        Base properties properties
   */
  public GroupedProperties(final Properties properties)
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
  public String[] groups()
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
  public boolean isGroup(final String prefix)
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
  public Properties subgroup(final String prefix)
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
