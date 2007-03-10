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


import dbconnector.Version;

/**
 * Utility for the command line.
 * 
 * @author sfatehi
 */
public class CommandLineUtility
{

  /**
   * Does a quick check of the command line arguments to find any
   * commonly used help options.
   * 
   * @param args
   *        Command line arguments.
   * @param helpResource
   *        Resource file containing help text.
   */
  public static void checkForHelp(final String[] args, final String helpResource)
  {
    boolean printUsage = false;
    if (args.length == 0)
    {
      printUsage = true;
    }
    for (final String arg: args)
    {
      if (arg.equalsIgnoreCase("-help") || arg.equalsIgnoreCase("-?")
          || arg.equalsIgnoreCase("--help"))
      {
        printUsage = true;
        break;
      }
    }
    if (printUsage)
    {
      final byte[] text = Utilities.readFully(CommandLineUtility.class
        .getResourceAsStream(helpResource));
      final String info = new String(text);

      System.out.println(Version.about());
      System.out.println(info);
      System.exit(0);
    }
  }

  private CommandLineUtility()
  {
    // Prevent instantiation
  }

}
