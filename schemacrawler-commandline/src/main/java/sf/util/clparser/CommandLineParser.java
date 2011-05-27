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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command-line options parser.
 * 
 * @author Steve Purcell, Sualeh Fatehi
 */
public class CommandLineParser
{

  private static final String DASH = "-";

  private final Map<String, Option<?>> optionsMap = new HashMap<String, Option<?>>();
  private Map<Option<?>, OptionValue<?>> optionValues = new HashMap<Option<?>, OptionValue<?>>();

  public CommandLineParser(final Option<?>... options)
  {
    for (final Option<?> option: options)
    {
      addOption(option);
    }
  }

  public final boolean getBooleanValue(final String optionName)
  {
    final OptionValue<Boolean> optionValue = getOptionValue(optionName);
    final Boolean value = optionValue.getValue();
    if (value == null)
    {
      return false;
    }
    else
    {
      return value;
    }
  }

  public final Integer getIntegerValue(final String optionName)
  {
    final OptionValue<Number> optionValue = getOptionValue(optionName);
    final Number value = optionValue.getValue();
    if (value == null)
    {
      return null;
    }
    else
    {
      return value.intValue();
    }
  }

  public final String getStringValue(final String optionName)
  {
    final OptionValue<?> optionValue = getOptionValue(optionName);
    final Object value = optionValue.getValue();
    if (value == null)
    {
      return null;
    }
    else
    {
      return String.valueOf(value);
    }
  }

  public final <T> T getValue(final String optionName)
  {
    final OptionValue<T> optionValue = getOptionValue(optionName);
    return optionValue.getValue();
  }

  public final boolean hasOptionValue(final String optionName)
  {
    final Option<?> option = optionsMap.get(DASH + optionName);
    if (option != null)
    {
      return optionValues.containsKey(option);
    }
    else
    {
      return false;
    }
  }

  /**
   * Extract the options and non-option arguments from the given list of
   * command-line arguments. The default locale is used for parsing
   * options whose values might be locale-specific.
   * 
   * @param args
   *        Command line arguments
   */
  public final String[] parse(final String[] args)
  {
    optionValues = new HashMap<Option<?>, OptionValue<?>>();

    final List<String> remainingArgs = new ArrayList<String>();
    int position = 0;
    while (position < args.length)
    {
      String currentArg = args[position];
      String valueArg = null;

      // handle -arg=value
      final int equalsPos = currentArg.indexOf('=');

      if (equalsPos != -1)
      {
        valueArg = currentArg.substring(equalsPos + 1);
        currentArg = currentArg.substring(0, equalsPos);
      }

      final Option<?> option = optionsMap.get(currentArg);
      if (option == null || optionValues.containsKey(option))
      {
        remainingArgs.add(args[position]);
        position++;
        continue;
      }

      if (valueArg == null)
      {
        // The next argument is the value argument
        position++;
        if (position < args.length)
        {
          valueArg = args[position];
          // If this is not an argument, backtrack
          if (valueArg.startsWith(DASH))
          {
            position--;
            valueArg = null;
          }
        }
      }
      // Booleans are true, even if they have no value
      if (valueArg == null && option instanceof BooleanOption)
      {
        valueArg = Boolean.TRUE.toString();
      }

      final OptionValue<?> optionValue = ((BaseOption<?>) option)
        .parseValue(valueArg);
      if (optionValue != null)
      {
        optionValues.put(option, optionValue);
      }

      position++;
    }

    final String[] unparsedArgs = remainingArgs
      .toArray(new String[remainingArgs.size()]);
    return unparsedArgs;
  }

  @Override
  public String toString()
  {
    return optionValues.values().toString();
  }

  /**
   * Add the specified Option to the list of accepted options.
   * 
   * @param option
   *        Option to add
   */
  protected void addOption(final Option<?> option)
  {
    if (option == null)
    {
      return;
    }
    if (option.hasShortForm())
    {
      optionsMap.put(DASH + option.getShortForm(), option);
    }
    if (option.hasLongForm())
    {
      optionsMap.put(DASH + option.getLongForm(), option);
    }
  }

  private final <T> OptionValue<T> getOptionValue(final String optionName)
  {
    OptionValue<T> optionValue = null;
    final Option<T> option = (Option<T>) optionsMap.get(DASH + optionName);
    if (option != null)
    {
      optionValue = (OptionValue<T>) optionValues.get(option);
      if (optionValue == null)
      {
        optionValue = new OptionValue<T>(option, null);
      }
    }
    return optionValue;
  }

}
