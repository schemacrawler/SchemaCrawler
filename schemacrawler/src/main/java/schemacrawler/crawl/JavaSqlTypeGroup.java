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
package schemacrawler.crawl;


import java.math.BigDecimal;

public enum JavaSqlTypeGroup
{

  unknown(Void.class),
  binary(byte[].class),
  bit(Boolean.class),
  character(String.class),
  id("java.sql.RowId"),
  integer(Integer.class),
  real(BigDecimal.class),
  reference(java.sql.Ref.class),
  temporal(java.sql.Timestamp.class),
  url(java.net.URL.class),
  xml("java.sql.SQLXML");

  private final Class<?> javaSqlTypeGroupClass;

  private JavaSqlTypeGroup(final Class<?> javaSqlTypeGroupClass)
  {
    this.javaSqlTypeGroupClass = javaSqlTypeGroupClass;
  }

  private JavaSqlTypeGroup(final String javaSqlTypeGroupClassName)
  {
    Class<?> javaSqlTypeGroupClass;
    try
    {
      javaSqlTypeGroupClass = Class.forName(javaSqlTypeGroupClassName);
    }
    catch (final Throwable e)
    {
      javaSqlTypeGroupClass = null;
    }
    this.javaSqlTypeGroupClass = javaSqlTypeGroupClass;
  }

  public Class<?> getJavaSqlTypeGroupClass()
  {
    return javaSqlTypeGroupClass;
  }

  @Override
  public String toString()
  {
    return String.format("%s\t%s",
                         name(),
                         javaSqlTypeGroupClass != null? javaSqlTypeGroupClass
                           .getCanonicalName(): "");
  }

}
