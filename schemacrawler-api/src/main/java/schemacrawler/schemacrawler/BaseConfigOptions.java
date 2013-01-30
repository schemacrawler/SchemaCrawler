/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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

package schemacrawler.schemacrawler;


/**
 * Options.
 * 
 * @author Sualeh Fatehi
 */
public abstract class BaseConfigOptions
  implements Options
{

  private static final long serialVersionUID = -8133661515343358712L;

  private final Config config;

  protected BaseConfigOptions()
  {
    config = new Config();
  }

  public Config toConfig()
  {
    return new Config(config);
  }

  @Override
  public String toString()
  {
    return config.toString();
  }

  protected boolean getBooleanValue(final Config config,
                                    final String propertyName)
  {
    return getBooleanValue(config, propertyName, false);
  }

  protected boolean getBooleanValue(final Config config,
                                    final String propertyName,
                                    final boolean defaultValue)
  {
    if (config == null)
    {
      return defaultValue;
    }
    return config.getBooleanValue(propertyName);
  }

  protected boolean getBooleanValue(final String propertyName)
  {
    return getBooleanValue(propertyName, false);
  }

  protected boolean getBooleanValue(final String propertyName,
                                    final boolean defaultValue)
  {
    return getBooleanValue(config, propertyName, defaultValue);
  }

  protected <E extends Enum<E>> E getEnumValue(final Config config,
                                               final String propertyName,
                                               final E defaultValue)
  {
    if (config == null)
    {
      return defaultValue;
    }
    return config.getEnumValue(propertyName, defaultValue);
  }

  protected <E extends Enum<E>> E getEnumValue(final String propertyName,
                                               final E defaultValue)
  {
    return getEnumValue(config, propertyName, defaultValue);
  }

  protected int getIntegerValue(final Config config,
                                final String propertyName,
                                final int defaultValue)
  {
    if (config == null)
    {
      return defaultValue;
    }
    return config.getIntegerValue(propertyName, defaultValue);
  }

  protected int getIntegerValue(final String propertyName,
                                final int defaultValue)
  {
    return getIntegerValue(config, propertyName, defaultValue);
  }

  protected String getStringValue(final Config config,
                                  final String propertyName,
                                  final String defaultValue)
  {
    if (config == null)
    {
      return defaultValue;
    }
    return config.getStringValue(propertyName, defaultValue);
  }

  protected String getStringValue(final String propertyName,
                                  final String defaultValue)
  {
    return getStringValue(config, propertyName, defaultValue);
  }

  protected void setBooleanValue(final String propertyName, final boolean value)
  {
    config.put(propertyName, Boolean.toString(value));
  }

  protected <E extends Enum<E>> void setEnumValue(final String propertyName,
                                                  final E value)
  {
    config.put(propertyName, value.name());
  }

  protected void setIntegerValue(final String propertyName, final int value)
  {
    config.put(propertyName, Integer.toString(value));
  }

  protected void setStringValue(final String propertyName, final String value)
  {
    config.put(propertyName, value);
  }
}
