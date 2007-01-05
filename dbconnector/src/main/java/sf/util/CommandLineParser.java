/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
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


import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Command-line options parser.
 * 
 * @author Steve Purcell, Sualeh Fatehi
 */
public final class CommandLineParser
{

  private static final String DASH = "-";
  private String[] remainingArgs;
  private final Map optionsMap = new HashMap();

  /**
   * Add the specified Option to the list of accepted options.
   * 
   * @param option
   *        Option to add
   */
  public void addOption(final BaseOption option)
  {
    if (option.hasShortForm())
    {
      optionsMap.put(DASH + option.getShortForm(), option);
    }
    if (option.hasLongForm())
    {
      optionsMap.put(DASH + option.getLongForm(), option);
    }
  }

  /**
   * Remaining arguments, that are not parsed.
   * 
   * @return The non-option arguments
   */
  public String[] getRemainingArgs()
  {
    final String[] remainingArgsCopy = new String[remainingArgs.length];
    System.arraycopy(remainingArgs,
                     0,
                     remainingArgsCopy,
                     0,
                     remainingArgs.length);
    return remainingArgsCopy;
  }

  /**
   * Get all the command line options.
   * 
   * @return Command line options
   */
  public BaseOption[] getOptions()
  {
    final Collection options = optionsMap.values();
    final Collection uniqueOptions = new HashSet();

    for (final Iterator iterator = options.iterator(); iterator.hasNext();)
    {
      final BaseOption option = (BaseOption) iterator.next();
      if (!uniqueOptions.contains(option))
      {
        uniqueOptions.add(option);
      }
    }

    return (BaseOption[]) uniqueOptions.toArray(new BaseOption[uniqueOptions
      .size()]);
  }

  /**
   * Get an option by name.
   * 
   * @param optionName
   *        Name of the option
   * @return Option
   */
  public BaseOption getOption(final String optionName)
  {
    return (BaseOption) optionsMap.get(DASH + optionName);
  }

  /**
   * Extract the options and non-option arguments from the given list of
   * command-line arguments. The default locale is used for parsing
   * options whose values might be locale-specific.
   * 
   * @param args
   *        Command line arguments
   */
  public void parse(final String[] args)
  {

    // clean out all options
    final BaseOption[] options = getOptions();
    for (int i = 0; i < options.length; i++)
    {
      options[i].reset();
    }

    final List otherArgs = new Vector();

    int position = 0;

    while (position < args.length)
    {
      String currentArg = args[position];
      String valueArg = null;

      // handle -arg=value
      final int equalsPos = currentArg.indexOf("=");

      if (equalsPos != -1)
      {
        valueArg = currentArg.substring(equalsPos + 1);
        currentArg = currentArg.substring(0, equalsPos);
      }

      final BaseOption option = (BaseOption) optionsMap.get(currentArg);

      if (option == null)
      {
        otherArgs.add(currentArg);
        position++;

        continue;
      }

      if (option.wantsValue())
      {
        if (valueArg == null)
        {
          // the next argument is the value argument
          position++;

          if (position < args.length)
          {
            valueArg = args[position];
          }
        }
      }
      else
      {
        valueArg = Boolean.TRUE.toString();
      }

      option.setValue(valueArg);
      position++;
    }

    remainingArgs = (String[]) otherArgs.toArray(new String[otherArgs.size()]);
  }

  /**
   * Representation of a command-line option.
   */
  public abstract static class BaseOption
  {

    protected String shortForm;
    protected String longForm;
    protected boolean hasShortForm;
    protected boolean hasLongForm;
    protected boolean wantsValue;
    protected Object value;

    protected BaseOption(final char shortForm, final boolean wantsValue)
    {
      setShortForm(shortForm);

      this.wantsValue = wantsValue;
    }

    protected BaseOption(final String longForm, final boolean wantsValue)
    {
      setLongForm(longForm);

      this.wantsValue = wantsValue;
    }

    protected BaseOption(final char shortForm,
                         final String longForm,
                         final boolean wantsValue)
    {
      setShortForm(shortForm);
      setLongForm(longForm);

      this.wantsValue = wantsValue;
    }

    private void setShortForm(final char shortForm)
    {
      this.shortForm = new String(new char[] {
        shortForm
      });
      hasShortForm = true;
    }

    private void setLongForm(final String longForm)
    {
      if (longForm == null || longForm.length() == 0)
      {
        throw new IllegalArgumentException("Long form for option not specified");
      }
      this.longForm = longForm;
      hasLongForm = true;
    }

    /**
     * Gets the short form of the switch for the option.
     * 
     * @return Short form of the switch
     */
    public String getShortForm()
    {
      return shortForm;
    }

    /**
     * Gets the long form of the switch for the option.
     * 
     * @return Long form of the switch
     */
    public String getLongForm()
    {
      return longForm;
    }

    /**
     * Whether the option has the short form of the switch.
     * 
     * @return Whether the option has the short form of the switch
     */
    public boolean hasShortForm()
    {
      return hasShortForm;
    }

    /**
     * Whether the option has the long form of the switch.
     * 
     * @return Whether the option has the long form of the switch
     */
    public boolean hasLongForm()
    {
      return hasLongForm;
    }

    /**
     * Whether the option was found.
     * 
     * @return Whether the option was found
     */
    public boolean isFound()
    {
      if (value instanceof Boolean)
      {
        return ((Boolean) value).booleanValue();
      }
      return value != null;
    }

    void reset()
    {
      if (value instanceof Boolean)
      {
        value = Boolean.FALSE;
      }
      else
      {
        value = null;
      }
    }

    /**
     * Whether or not this option wants a value.
     * 
     * @return Whether or not this option wants a value
     */
    public final boolean wantsValue()
    {
      return wantsValue;
    }

    /**
     * Gets the value for the option.
     * 
     * @return Option value
     */
    public Object getValue()
    {
      return value;
    }

    protected final void setValue(final String valueString)
    {
      if (wantsValue && valueString == null)
      {
        value = null;
      }
      else
      {
        value = parseValue(valueString);
      }
    }

    /**
     * Override to extract and convert an option value passed on the
     * command-line.
     * 
     * @param valueString
     * @return Parsed value
     */
    protected abstract Object parseValue(final String valueString);

  }

  /**
   * An option that expects a boolean value.
   */
  public static final class BooleanOption
    extends BaseOption
  {

    /**
     * Constructor that takes the short form of the switch only.
     * 
     * @param shortForm
     *        Short form of the switch
     */
    public BooleanOption(final char shortForm)
    {
      super(shortForm, false);
      value = Boolean.FALSE;
    }

    /**
     * Constructor that takes the long form of the switch only.
     * 
     * @param longForm
     *        Long form of the switch
     */
    public BooleanOption(final String longForm)
    {
      super(longForm, false);
      value = Boolean.FALSE;
    }

    /**
     * Constructor that takes the short form and long form of the
     * switch.
     * 
     * @param shortForm
     *        Short form of the switch
     * @param longForm
     *        Long form of the switch
     */
    public BooleanOption(final char shortForm, final String longForm)
    {
      super(shortForm, longForm, false);
      value = Boolean.FALSE;
    }

    protected Object parseValue(final String valueString)
    {
      if (valueString == null || valueString.length() == 0)
      {
        return Boolean.FALSE;
      }
      return Boolean.valueOf(valueString);
    }
  }

  /**
   * An option that expects a floating-point value.
   */
  public static final class NumberOption
    extends BaseOption
  {

    /**
     * Constructor that takes the short form of the switch only.
     * 
     * @param shortForm
     *        Short form of the switch
     */
    public NumberOption(final char shortForm)
    {
      super(shortForm, true);
    }

    /**
     * Constructor that takes the long form of the switch only.
     * 
     * @param longForm
     *        Long form of the switch
     */
    public NumberOption(final String longForm)
    {
      super(longForm, true);
    }

    /**
     * Constructor that takes the short form and long form of the
     * switch.
     * 
     * @param shortForm
     *        Short form of the switch
     * @param longForm
     *        Long form of the switch
     */
    public NumberOption(final char shortForm, final String longForm)
    {
      super(shortForm, longForm, true);
    }

    protected Object parseValue(final String arg)
    {
      try
      {
        return NumberFormat.getNumberInstance().parse(arg);
      }
      catch (final ParseException e)
      {
        return null;
      }
    }
  }

  /**
   * An option that expects a string value.
   */
  public static final class StringOption
    extends BaseOption
  {

    /**
     * Constructor that takes the short form of the switch only.
     * 
     * @param shortForm
     *        Short form of the switch
     */
    public StringOption(final char shortForm)
    {
      super(shortForm, true);
    }

    /**
     * Constructor that takes the long form of the switch only.
     * 
     * @param longForm
     *        Long form of the switch
     */
    public StringOption(final String longForm)
    {
      super(longForm, true);
    }

    /**
     * Constructor that takes the short form and long form of the
     * switch.
     * 
     * @param shortForm
     *        Short form of the switch
     * @param longForm
     *        Long form of the switch
     */
    public StringOption(final char shortForm, final String longForm)
    {
      super(shortForm, longForm, true);
    }

    protected Object parseValue(final String arg)
    {
      return arg;
    }
  }

}
