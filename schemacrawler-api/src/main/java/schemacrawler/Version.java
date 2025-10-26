/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler;

import java.io.Serial;
import us.fatehi.utility.property.BaseProductVersion;

/**
 * Version information for this product. Has methods to obtain information about the product, as
 * well as a main method, so it can be called from the command-line.
 */
public final class Version extends BaseProductVersion {

  @Serial private static final long serialVersionUID = 1143606778430634288L;

  private static final String ABOUT =
      """
      SchemaCrawler 17.1.2
      Database schema discovery and comprehension tool
      https://www.schemacrawler.com/
      Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.

      You can search for database schema objects using regular expressions,
      and output the schema and data in a readable text format. You can find
      potential schema design issues with lint. The output serves for
      database documentation is designed to be diff-ed against other database
      schemas. SchemaCrawler also generates schema diagrams.
      """
          .stripIndent();

  private static final Version VERSION = new Version("SchemaCrawler", "17.1.2");

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
