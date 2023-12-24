/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.schemacrawler;

/** SchemaCrawler options builder, to build the immutable options to crawl a schema. */
public final class SchemaCrawlerOptionsBuilder {

  public static SchemaCrawlerOptions newSchemaCrawlerOptions() {
    final LimitOptions limitOptions = LimitOptionsBuilder.newLimitOptions();
    final FilterOptions filterOptions = FilterOptionsBuilder.newFilterOptions();
    final GrepOptions grepOptions = GrepOptionsBuilder.newGrepOptions();
    final LoadOptions loadOptions = LoadOptionsBuilder.newLoadOptions();
    return new SchemaCrawlerOptions(limitOptions, filterOptions, grepOptions, loadOptions);
  }

  private SchemaCrawlerOptionsBuilder() {
    throw new UnsupportedOperationException();
  }
}
