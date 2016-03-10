/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2016, Sualeh Fatehi.
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
package schemacrawler.tools.lint;


import java.util.logging.Level;
import java.util.logging.Logger;

public enum LintDispatch
{
 none
 {
   @Override
   public void dispatch()
   {
     LOGGER.log(Level.FINE, dispatchMessage);
   }
 },
 write_err
 {
   @Override
   public void dispatch()
   {
     System.err.println(dispatchMessage);
   }
 },
 throw_exception
 {
   @Override
   public void dispatch()
   {
     throw new RuntimeException(dispatchMessage);
   }
 },
 terminate_system
 {
   @Override
   public void dispatch()
   {
     LOGGER.log(Level.SEVERE, dispatchMessage);
     System.exit(1);
   }
 },;

  private static final Logger LOGGER = Logger
    .getLogger(LintDispatch.class.getName());

  private static final String dispatchMessage = "Too many schema lints were found";

  public abstract void dispatch();

}
