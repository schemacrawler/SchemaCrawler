/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


/**
 * Version information for this product. Has methods to obtain
 * information about the product, as well as a main method, so it can be
 * called from the command line.
 * 
 * @author Sualeh Fatehi
 */
public final class Version
{

  private static final String PRODUCTNAME = "SchemaCrawler";
  private static final String VERSION = "6.2";

  /**
   * Information about this product.
   * 
   * @return Information about this product.
   */
  public static String about()
  {
    return String.format("%s %s%nCopyright (c) 2000-2009, Sualeh Fatehi.",
                         PRODUCTNAME,
                         VERSION);
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

  /**
   * {@inheritDoc}
   * 
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return about();
  }

}
