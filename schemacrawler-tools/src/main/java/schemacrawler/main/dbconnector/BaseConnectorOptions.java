/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General public final License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General public final License for more details.
 *
 * You should have received a copy of the GNU Lesser General public final License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.main.dbconnector;


import schemacrawler.schemacrawler.Options;
import sf.util.Utilities;

/**
 * Additional options needed for Spring.
 * 
 * @author Sualeh Fatehi
 */
abstract class BaseConnectorOptions
  implements Options
{

  private static final long serialVersionUID = 5125868244511892692L;

  private String schemapattern;
  private String user;
  private String password;

  public final String getPassword()
  {
    return password;
  }

  public final String getSchemapattern()
  {
    return schemapattern;
  }

  public final String getUser()
  {
    return user;
  }

  public final boolean hasPassword()
  {
    return password != null;
  }

  public final boolean hasSchemaPattern()
  {
    return !Utilities.isBlank(schemapattern);
  }

  public final boolean hasUser()
  {
    return !Utilities.isBlank(user);
  }

  public final void setPassword(final String password)
  {
    this.password = password;
  }

  public final void setSchemapattern(final String schemapattern)
  {
    this.schemapattern = schemapattern;
  }

  public final void setUser(final String user)
  {
    this.user = user;
  }

}
