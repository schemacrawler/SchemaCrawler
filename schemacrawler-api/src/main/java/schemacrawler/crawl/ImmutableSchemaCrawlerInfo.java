/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
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
  extends BaseProductVersion
  implements SchemaCrawlerInfo
{

  private static final long serialVersionUID = 4051323422934251828L;

  private final String schemaCrawlerAbout;

  ImmutableSchemaCrawlerInfo()
  {
    super(Version.getProductName(), Version.getVersion());
    schemaCrawlerAbout = Version.about();
  }

  @Override
  public String getSchemaCrawlerAbout()
  {
    return schemaCrawlerAbout;
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated
   */
  @Override
  @Deprecated
  public String getSchemaCrawlerProductName()
  {
    return getProductName();
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated
   */
  @Override
  @Deprecated
  public String getSchemaCrawlerVersion()
  {
    return getProductVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return schemaCrawlerAbout;
  }

}
