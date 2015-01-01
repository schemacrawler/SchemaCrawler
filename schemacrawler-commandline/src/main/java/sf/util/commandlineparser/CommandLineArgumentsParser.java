/*
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
 * This library is free software; you can redistribute it and/or modify it under
 * the terms
 * of the GNU Lesser General Public License as published by the Free Software
 * Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package sf.util.commandlineparser;


import static java.util.Objects.requireNonNull;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Command-line options parser. Not POSIX compliant.<br/>
 * Follows these POSIX rules:
 * <ul>
 * <li>Arguments are options if they begin with a hyphen delimiter
 * ('-').</li>
 * <li>Certain options require an argument. For example, the '-o'
 * command of the ld command requires an argument—an output file name.</li>
 * <li>Options typically precede other non-option arguments.</li>
 * <li>Options may be supplied in any order, or appear multiple times.
 * The interpretation is left up to the particular application program.</li>
 * </ul>
 * Does not honor these POSIX rules:
 * <ul>
 * <li>Multiple options may follow a hyphen delimiter in a single token
 * if the options do not take arguments. Thus, '-abc' is equivalent to
 * '-a -b -c'.</li>
 * <li>Option names are single alphanumeric characters (as for isalnum;
 * see Classification of Characters).</li>
 * <li>An option and its argument may or may not appear as separate
 * tokens. (In other words, the whitespace separating them is optional.)
 * Thus, '-o foo' and '-ofoo' are equivalent.</li>
 * <li>The argument '--' terminates all options; any following arguments
 * are treated as non-option arguments, even if they begin with a
 * hyphen.</li>
 * <li>A token consisting of a single hyphen character is interpreted as
 * an ordinary non-option argument. By convention, it is used to specify
 * input from or output to the standard input and output streams.</li>
 * </ul>
 */
public class CommandLineArgumentsParser
{

  private static final String DASH = "-";

  private final String[] args;
  private final Map<String, String> optionsMap;
  private final List<String> nonOptionArguments;

  public CommandLineArgumentsParser(final String[] args)
  {
    this.args = requireNonNull(args);
    optionsMap = new HashMap<>();
    nonOptionArguments = new ArrayList<>();
  }

  public List<String> getNonOptionArguments()
  {
    return nonOptionArguments;
  }

  public Map<String, String> getOptionsMap()
  {
    return optionsMap;
  }

  /**
   * Extract the options and non-option arguments from the given list of
   * command-line arguments.
   */
  public void parse()
  {
    final Deque<String> argsList = new ArrayDeque<>(Arrays.asList(args));
    while (true)
    {
      final String currentArg = argsList.pollFirst();
      if (currentArg == null)
      {
        if (argsList.isEmpty())
        {
          break;
        }
        else
        {
          continue;
        }
      }
      if (currentArg.startsWith(DASH))
      {
        // Handle -arg=value
        if (currentArg.contains("="))
        {
          final String[] split = currentArg.split("=");
          final String option = split[0].replaceAll("^-+", "");
          final String value;
          if (split.length == 2)
          {
            value = split[1];
          }
          else
          {
            value = null;
          }
          optionsMap.put(option, value);
        }
        else
        {
          // Look at the next argument, and if is an option, that means
          // there is no value for the current option
          final String option = currentArg.replaceAll("^-+", "");
          final String value = argsList.peekFirst();
          if (value != null && value.startsWith(DASH))
          {
            optionsMap.put(option, null);
          }
          else
          {
            optionsMap.put(option, argsList.pollFirst());
          }
        }
      }
      else
      {
        nonOptionArguments.add(currentArg);
      }
    }
  }

  @Override
  public String toString()
  {
    return Arrays.toString(args);
  }

}
