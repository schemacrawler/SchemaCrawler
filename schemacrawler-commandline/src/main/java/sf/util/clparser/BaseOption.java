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

package sf.util.clparser;


/**
 * Representation of a command-line option.
 * 
 * @author Sualeh Fatehi
 * @param <T>
 *        Option type
 */
public abstract class BaseOption<T>
  implements Option<T>
{

  private final Character shortForm;
  private final String longForm;
  private final T defaultValue;

  protected BaseOption(final Character shortForm,
                       final String longForm,
                       final T defaultValue)
  {
    this.shortForm = shortForm;
    this.longForm = longForm;

    if (this.shortForm == null && this.longForm == null)
    {
      throw new IllegalArgumentException("Command line option is not defined");
    }

    this.defaultValue = defaultValue;
  }

  protected BaseOption(final Character shortForm, final T defaultValue)
  {
    this(shortForm, null, defaultValue);
  }

  protected BaseOption(final String longForm, final T defaultValue)
  {
    this(null, longForm, defaultValue);
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof BaseOption))
    {
      return false;
    }
    final BaseOption other = (BaseOption) obj;
    if (defaultValue == null)
    {
      if (other.defaultValue != null)
      {
        return false;
      }
    }
    else if (!defaultValue.equals(other.defaultValue))
    {
      return false;
    }
    if (longForm == null)
    {
      if (other.longForm != null)
      {
        return false;
      }
    }
    else if (!longForm.equals(other.longForm))
    {
      return false;
    }
    if (shortForm == null)
    {
      if (other.shortForm != null)
      {
        return false;
      }
    }
    else if (!shortForm.equals(other.shortForm))
    {
      return false;
    }
    return true;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Option#getDefaultValue()
   */
  @Override
  public T getDefaultValue()
  {
    return defaultValue;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Option#getLongForm()
   */
  @Override
  public String getLongForm()
  {
    return longForm;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Option#getShortForm()
   */
  @Override
  public String getShortForm()
  {
    return shortForm.toString();
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result
             + (defaultValue == null? 0: defaultValue.hashCode());
    result = prime * result + (longForm == null? 0: longForm.hashCode());
    result = prime * result + (shortForm == null? 0: shortForm.hashCode());
    return result;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Option#hasLongForm()
   */
  @Override
  public boolean hasLongForm()
  {
    return longForm != null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see Option#hasShortForm()
   */
  @Override
  public boolean hasShortForm()
  {
    return shortForm != null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final String optionString = "{"
                                + (hasShortForm()? "-" + shortForm: "")
                                + (hasLongForm()? "/ -" + longForm: "")
                                + (defaultValue != null? " (" + defaultValue
                                                         + ")": "") + "}";
    return optionString;
  }

  protected abstract OptionValue<T> parseValue(final String valueString);

}
