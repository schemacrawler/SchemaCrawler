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

package schemacrawler.tools.integration.velocity;


import schemacrawler.Version;
import sf.util.Utilities;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main
{

  /**
   * Internal storage for information. Read from text file.
   */
  private static String info;

  static
  {
    final byte[] text = Utilities.readFully(Main.class
      .getResourceAsStream("/schemacrawler-templating-readme.txt"));
    info = new String(text);

  }

  private Main()
  {
  }

  private static void printUsage()
  {
    System.out.println(Version.about());
    System.out.println(info);
  }

  /**
   * s Get connection parameters, and creates a connection, and crawls
   * the schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  public static void main(final String[] args)
    throws Exception
  {

    if (args.length == 0)
    {
      printUsage();
      return;
    }

    schemacrawler.Main.doMain(args, new VelocityExecutor());

  }

}
