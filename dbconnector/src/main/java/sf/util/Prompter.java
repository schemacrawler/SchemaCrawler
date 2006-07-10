/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2006, Sualeh Fatehi.
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


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Prompts for information.
 */
public class Prompter
{

  /**
   * String input type.
   */
  public static final InputType STRING = new InputType("STRING");
  /**
   * Number input type.
   */
  public static final InputType NUMBER = new InputType("NUMBER");

  private final PrintWriter out;
  private final BufferedReader in;

  /**
   * Creates a new prompter for standard input and output.
   */
  public Prompter()
  {
    out = new PrintWriter(System.out, true);
    in = new BufferedReader(new InputStreamReader(System.in));
  }

  /**
   * Primpts, and gets the input for a datum.
   * 
   * @param prompt
   *          Prompt
   * @param defaultValue
   *          Default value
   * @param type
   *          Expected type of value
   * @param allowEmptyStrings
   *          Whether to allow empty strings
   * @return The input
   */
  public Object getInput(final String prompt, final Object defaultValue,
                         final InputType type, final boolean allowEmptyStrings)
  {

    Object answer = defaultValue;
    boolean isValid = false;

    // loop until input type is valid
    while (!isValid)
    {

      isValid = true;

      // Get input
      out.print(prompt);
      if (defaultValue != null && defaultValue.toString().length() > 0)
      {
        out.print(" [" + defaultValue + "]");
      }
      out.println();
      out.print(" > ");
      out.flush();

      try
      {
        answer = in.readLine();
      }
      catch (final IOException e)
      {
        answer = defaultValue;
      }
      if (answer == null
          || answer.toString().length() == 0 && defaultValue != null)
      {
        answer = defaultValue;
      }

      // test input type
      if (type == Prompter.STRING
          && answer.toString().length() == 0 && !allowEmptyStrings)
      {
        isValid = false;
      }
      else if (type == Prompter.NUMBER)
      {
        // test that this is an number
        try
        {
          Double.parseDouble(answer.toString());
        }
        catch (final NumberFormatException e)
        {
          isValid = false;
        }
      }

    }

    if (type == Prompter.STRING
        && answer.toString().length() == 0 && !allowEmptyStrings)
    {
      answer = answer.toString();
    }
    else if (type == Prompter.NUMBER)
    {
      // test that this is an number
      try
      {
        answer = new Double(Double.parseDouble(answer.toString()));
      }
      catch (final NumberFormatException e)
      {
        answer = new Double(0);
      }
    }

    return answer;

  }

  /**
   * Display a menu of options to choose from.
   * 
   * @param prompt
   *          Prompt
   * @param menu
   *          List of menu display values
   * @param values
   *          List of menu display values
   * @param defaultValue
   *          Default value
   * @return Selected value
   */
  public String getInputWithMenu(final String prompt, final String[] menu,
                                 final String[] values,
                                 final String defaultValue)
  {

    String answer = defaultValue;
    boolean isValid = false;

    // loop until input type is valid
    while (!isValid)
    {

      isValid = true;

      // get input
      out.print(prompt);
      if (defaultValue != null && defaultValue.length() > 0)
      {
        out.print(" [" + defaultValue + "]");
      }
      out.println();

      for (int i = 0; i < menu.length; i++)
      {
        out.println("    " + (i + 1) + ". " + menu[i]);
      }
      out.print("  > ");

      try
      {
        answer = in.readLine();
      }
      catch (final IOException e)
      {
        out.println(e.getMessage());
        out.println();
        isValid = false;
        continue;
      }

      if (answer == null || answer.length() == 0 && defaultValue == null)
      {
        out.println("Invalid choice");
        out.println();
        isValid = false;
        continue;
      }

      if (answer.length() == 0)
      {
        answer = defaultValue;
      }

      // test input type
      for (int i = 0; i < menu.length; i++)
      {
        if (answer.equals(Integer.toString(i + 1)))
        {
          isValid = true;
          answer = values[i];
          break;
        }
      }

      out.println("Invalid choice: " + answer);
      out.println();

    }

    return answer;

  }

  private static final class InputType
  {

    private final String inputType; // for debug only

    private InputType(final String name)
    {
      inputType = name;
    }

    /**
     * {@inheritDoc}
     * 
     * @see Object#toString()
     */
    public String toString()
    {
      return inputType;
    }
  }

}
