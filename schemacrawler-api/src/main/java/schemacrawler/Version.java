/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2021, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static us.fatehi.utility.IOUtility.readResourceFully;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Version information for this product. Has methods to obtain information about the product, as
 * well as a main method, so it can be called from the command-line.
 */
public final class Version extends BaseProductVersion {

  private static final long serialVersionUID = 1143606778430634288L;

  private static final String ABOUT;
  private static final String PRODUCT_NAME = "SchemaCrawler";
  private static final String PRODUCT_VERSION;

  static {
    ABOUT = readResourceFully("/help/SchemaCrawler.txt");

    String[] productLine;
    try (final BufferedReader reader = new BufferedReader(new StringReader(ABOUT))) {
      final String readLine = reader.readLine();
      if (readLine != null) {
        productLine = readLine.split(" ");
      } else {
        productLine = new String[] {PRODUCT_NAME, ""};
      }
    } catch (final IOException e) {
      productLine = new String[] {PRODUCT_NAME, ""};
    }
    PRODUCT_VERSION = productLine[1];
  }

  /**
   * Information about this product.
   *
   * @return Information about this product.
   */
  public static String about() {
    return ABOUT;
  }

  /**
   * Main routine. Prints information about this product.
   *
   * @param args Arguments to the main routine - they are ignored.
   */
  public static void main(final String[] args) {
    System.out.println(about());
  }

  public static Version version() {
    return new Version();
  }

  private Version() {
    super(PRODUCT_NAME, PRODUCT_VERSION);
  }
}
