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


import schemacrawler.schema.SchemaCrawlerInfo;

/**
 * SchemaCrawler information.
 * 
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class MutableSchemaCrawlerInfo
  implements SchemaCrawlerInfo
{

  private static final long serialVersionUID = 4051323422934251828L;

  private final String schemaCrawlerProductName;
  private final String schemaCrawlerVersion;
  private final String schemaCrawlerAbout;

  public MutableSchemaCrawlerInfo(final String schemaCrawlerProductName,
                                  final String schemaCrawlerVersion,
                                  final String schemaCrawlerAbout)
  {
    this.schemaCrawlerProductName = schemaCrawlerProductName;
    this.schemaCrawlerVersion = schemaCrawlerVersion;
    this.schemaCrawlerAbout = schemaCrawlerAbout;
  }

  public String getSchemaCrawlerAbout()
  {
    return schemaCrawlerAbout;
  }

  public String getSchemaCrawlerProductName()
  {
    return schemaCrawlerProductName;
  }

  public String getSchemaCrawlerVersion()
  {
    return schemaCrawlerVersion;
  }

}
