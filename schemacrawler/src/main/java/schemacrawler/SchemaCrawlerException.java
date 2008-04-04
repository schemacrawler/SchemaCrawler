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

package schemacrawler;


/**
 * Exception for the crawler.
 */
public class SchemaCrawlerException
  extends Exception
{

  private static final long serialVersionUID = 3257848770627713076L;

  /**
   * General exception for SchemaCrawler.
   * 
   * @see Exception#Exception(java.lang.String)
   * @param message
   *        Exception message
   */
  public SchemaCrawlerException(final String message)
  {
    super(message);
  }

  /**
   * Constructs a new exception with the specified detail message and
   * cause.
   * 
   * @see Exception#Exception(java.lang.String, java.lang.Throwable)
   * @param message
   *        Error message
   * @param cause
   *        Cause of exception
   */
  public SchemaCrawlerException(final String message, final Throwable cause)
  {
    super(message + ": " + cause.getMessage(), cause);
  }

}
