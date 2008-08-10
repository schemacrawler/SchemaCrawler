/*
 * SchemaCrawler
 * Copyright (c) 2000-2008, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package schemacrawler.utility.datasource;


/**
 * Error creating or accessing a PropertiesDataSource.
 * 
 * @author Sualeh Fatehi
 */
public final class PropertiesDataSourceException
  extends Exception
{

  private static final long serialVersionUID = 4050761594017165621L;

  /**
   * Constructs a new exception with the specified detail message.
   * 
   * @param message
   *        the detail message. The detail message is saved for later
   *        retrieval by the {@link #getMessage()} method.
   */
  public PropertiesDataSourceException(final String message)
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
  public PropertiesDataSourceException(final String message,
                                       final Throwable cause)
  {
    super(message, cause);
  }

}
