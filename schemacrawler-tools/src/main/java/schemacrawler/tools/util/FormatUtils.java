/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
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

package schemacrawler.tools.util;


/**
 * Utility.
 * 
 * @author Sualeh Fatehi
 */
public final class FormatUtils
{

  /** Maximum output line length */
  public static final int MAX_LINE_LENGTH = 72;

  /**
   * Repeats a string.
   * 
   * @param string
   *        String to repeat
   * @param count
   *        Number of times to repeat
   * @return String with repetitions
   */
  public static String repeat(final String string, final int count)
  {

    String repeated = "";

    if (string != null && count >= 1)
    {
      final StringBuffer stringbuffer = new StringBuffer(string.length()
                                                         * count);
      for (int i = 0; i < count; i++)
      {
        stringbuffer.append(string);
      }
      repeated = stringbuffer.toString();
    }

    return repeated;

  }

  private FormatUtils()
  {
  }

}
