/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2015, Sualeh Fatehi.
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


import schemacrawler.Version;
import schemacrawler.schema.SchemaCrawlerInfo;

/**
 * SchemaCrawler information.
 *
 * @author Sualeh Fatehi sualeh@hotmail.com
 */
final class ImmutableSchemaCrawlerInfo
  implements SchemaCrawlerInfo
{

  private static final long serialVersionUID = 4051323422934251828L;

  private final String schemaCrawlerProductName;
  private final String schemaCrawlerVersion;
  private final String schemaCrawlerAbout;

  ImmutableSchemaCrawlerInfo()
  {
    schemaCrawlerProductName = Version.getProductName();
    schemaCrawlerVersion = Version.getVersion();
    schemaCrawlerAbout = Version.about();
  }

  @Override
  public String getSchemaCrawlerAbout()
  {
    return schemaCrawlerAbout;
  }

  @Override
  public String getSchemaCrawlerProductName()
  {
    return schemaCrawlerProductName;
  }

  @Override
  public String getSchemaCrawlerVersion()
  {
    return schemaCrawlerVersion;
  }

  /**
   * {@inheritDoc}
   *
   * @see Object#toString()
   */
  @Override
  public String toString()
  {
    return schemaCrawlerAbout;
  }

}
