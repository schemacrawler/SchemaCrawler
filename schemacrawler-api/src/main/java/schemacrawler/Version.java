/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;

/**
 * Version information for this product. Has methods to obtain information about the product, as
 * well as a main method, so it can be called from the command-line.
 */
public final class Version extends BaseProductVersion {

  private static final long serialVersionUID = 1143606778430634288L;

  private static final String ABOUT;
  private static final Version VERSION;

  static {
    try (final BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(
                Version.class.getResourceAsStream("/help/SchemaCrawler.txt"), UTF_8))) {

      final List<String> lines = reader.lines().collect(toList());
      lines.add("");

      final String productVersion = lines.get(0).split(" ")[1];
      VERSION = new Version("SchemaCrawler", productVersion);
      ABOUT = String.join(lineSeparator(), lines);

    } catch (final Exception e) {
      throw new InternalRuntimeException("Could not read internal information");
    }
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
    System.out.println(ABOUT);
  }

  public static Version version() {
    return VERSION;
  }

  private Version(final String productName, final String productVersion) {
    super(productName, productVersion);
  }
}
