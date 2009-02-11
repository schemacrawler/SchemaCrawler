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

package schemacrawler.main.dbconnector;


/**
 * Error creating or accessing a PropertiesDataSource.
 * 
 * @author Sualeh Fatehi
 */
public class DatabaseConnectorException
  extends Exception
{

  private static final long serialVersionUID = 4050761594017165621L;

  /**
   * Constructs a new exception with <code>null</code> as its detail
   * message.
   */
  public DatabaseConnectorException()
  {
    super();
  }

  /**
   * Constructs a new exception with the specified detail message.
   * 
   * @param message
   *        the detail message. The detail message is saved for later
   *        retrieval by the {@link #getMessage()} method.
   */
  public DatabaseConnectorException(final String message)
  {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and
   * cause. <p/> Note that the detail message associated with
   * <code>cause</code> is <i>not</i> automatically incorporated in this
   * exception's detail message.
   * 
   * @param message
   *        the detail message (which is saved for later retrieval by
   *        the {@link #getMessage()} method).
   * @param cause
   *        the cause (which is saved for later retrieval by the
   *        {@link #getCause()} method). (A <tt>null</tt> value is
   *        permitted, and indicates that the cause is nonexistent or
   *        unknown.)
   */
  public DatabaseConnectorException(final String message, final Throwable cause)
  {
    super(message, cause);
  }

  /**
   * Constructs a new exception with the specified cause and a detail
   * message of <tt>(cause==null ? null : cause.toString())</tt>.
   * 
   * @param cause
   *        Cause for the exception
   */
  public DatabaseConnectorException(final Throwable cause)
  {
    super(cause);
  }

}
