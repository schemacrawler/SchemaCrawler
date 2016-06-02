/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import sf.util.Utility;

/**
 * Version information for this product. Has methods to obtain
 * information about the product, as well as a main method, so it can be
 * called from the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class Version
{

  private static final String PRODUCTNAME = "SchemaCrawler";
  private static final String VERSION;
  private static final String ABOUT;

  static
  {
    ABOUT = Utility.readResourceFully("/help/SchemaCrawler.txt");

    String[] productLine;
    try
    {
      final String readLine = new BufferedReader(new StringReader(ABOUT))
        .readLine();
      if (readLine != null)
      {
        productLine = readLine.split(" ");
      }
      else
      {
        productLine = new String[] { PRODUCTNAME, "" };
      }
    }
    catch (final IOException e)
    {
      productLine = new String[] { PRODUCTNAME, "" };
    }
    VERSION = productLine[1];
  }

  /**
   * Information about this product.
   *
   * @return Information about this product.
   */
  public static String about()
  {
    return ABOUT;
  }

  /**
   * Product name.
   *
   * @return Product name.
   */
  public static String getProductName()
  {
    return PRODUCTNAME;
  }

  /**
   * Product version number.
   *
   * @return Product version number.
   */
  public static String getVersion()
  {
    return VERSION;
  }

  /**
   * Main routine. Prints information about this product.
   *
   * @param args
   *        Arguments to the main routine - they are ignored.
   */
  public static void main(final String[] args)
  {
    System.out.println(about());
  }

  private Version()
  {
    // Prevent external instantiation
  }

}
