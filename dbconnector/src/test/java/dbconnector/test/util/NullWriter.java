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

package dbconnector.test.util;


import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class NullWriter
  extends Writer
{

  private NullWriter()
  {
  }

  public void write(final int c)
    throws IOException
  {
  }

  public void write(final char cbuf[], final int off, final int len)
    throws IOException
  {
  }

  public void write(final String str, final int off, final int len)
    throws IOException
  {
  }

  public void flush()
    throws IOException
  {
  }

  public void close()
    throws IOException
  {
  }

  public static PrintWriter getNullPrintWriter()
  {
    return new PrintWriter(new NullWriter(), true);
  }

}
