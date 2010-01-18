/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2010, Sualeh Fatehi.
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

package schemacrawler;


import schemacrawler.tools.commandline.SchemaCrawlerMain;

/**
 * Main class that takes arguments for a database for crawling a schema.
 */
public final class Main {

  /**
   * Get connection parameters, and creates a connection, and crawls the schema.
   *
   * @param args Arguments passed into the program from the command line.
   *
   * @throws Exception On an exception
   */
  public static void main(final String[] args)
    throws Exception {
    SchemaCrawlerMain.main(args);
  }

  private Main() {
    // Prevent instantiation
  }

}
