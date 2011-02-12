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
public final class OptionValue<T>
  implements Option<T>
{

  private final Option<T> option;
  private final T value;

  public OptionValue(final Option<T> option, final T value)
  {
    this.option = option;
    this.value = value;
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
    if (!(obj instanceof OptionValue))
    {
      return false;
    }
    final OptionValue other = (OptionValue) obj;
    if (option == null)
    {
      if (other.option != null)
      {
        return false;
      }
    }
    else if (!option.equals(other.option))
    {
      return false;
    }
    if (value == null)
    {
      if (other.value != null)
      {
        return false;
      }
    }
    else if (!value.equals(other.value))
    {
      return false;
    }
    return true;
  }

  @Override
  public T getDefaultValue()
  {
    return option.getDefaultValue();
  }

  @Override
  public String getLongForm()
  {
    return option.getLongForm();
  }

  @Override
  public String getShortForm()
  {
    return option.getShortForm();
  }

  /**
   * {@inheritDoc}
   * 
   * @see Option#getValue()
   */
  public T getValue()
  {
    final T returnValue;
    if (!isFound())
    {
      returnValue = getDefaultValue();
    }
    else
    {
      returnValue = value;
    }
    return returnValue;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (option == null? 0: option.hashCode());
    result = prime * result + (value == null? 0: value.hashCode());
    return result;
  }

  @Override
  public boolean hasLongForm()
  {
    return option.hasLongForm();
  }

  @Override
  public boolean hasShortForm()
  {
    return option.hasShortForm();
  }

  /**
   * {@inheritDoc}
   * 
   * @see Option#isFound()
   */
  public boolean isFound()
  {
    return value != null;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    final String optionString = option.toString() + "=" + value;
    return optionString;
  }

}
