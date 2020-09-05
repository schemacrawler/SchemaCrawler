/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2020, Sualeh Fatehi <sualeh@hotmail.com>.
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


/**
 * SchemaCrawler options builder, to build the immutable options to crawl a
 * schema.
 */
public final class SchemaCrawlerOptionsBuilder
  implements OptionsBuilder<SchemaCrawlerOptionsBuilder, SchemaCrawlerOptions>, ConfigOptionsBuilder<SchemaCrawlerOptionsBuilder, SchemaCrawlerOptions>
{

  public static SchemaCrawlerOptionsBuilder builder()
  {
    return new SchemaCrawlerOptionsBuilder();
  }

  public static SchemaCrawlerOptions newSchemaCrawlerOptions()
  {
    return builder().toOptions();
  }

  private LimitOptions limitOptions;
  private FilterOptions filterOptions;
  private GrepOptions grepOptions;
  private LoadOptions loadOptions;

  /**
   * Default options.
   */
  private SchemaCrawlerOptionsBuilder()
  {
    limitOptions = LimitOptionsBuilder.newLimitOptions();
    filterOptions = FilterOptionsBuilder.newFilterOptions();
    grepOptions = GrepOptionsBuilder.newGrepOptions();
    loadOptions = LoadOptionsBuilder.newLoadOptions();
  }

  /**
   * Options from properties.
   *
   * @param config
   *   Configuration properties
   */
  @Override
  public SchemaCrawlerOptionsBuilder fromConfig(final Config config)
  {
    if (config == null)
    {
      return this;
    }

    // Load only inclusion rules for limit options
    limitOptions = LimitOptionsBuilder
      .builder()
      .fromOptions(limitOptions)
      .fromConfig(config)
      .toOptions();
    // Load only inclusion rules for grep options
    grepOptions = GrepOptionsBuilder
      .builder()
      .fromOptions(grepOptions)
      .fromConfig(config)
      .toOptions();

    return this;
  }

  @Override
  public SchemaCrawlerOptionsBuilder fromOptions(final SchemaCrawlerOptions options)
  {
    if (options == null)
    {
      return this;
    }

    limitOptions = options.getLimitOptions();
    filterOptions = options.getFilterOptions();
    grepOptions = options.getGrepOptions();
    loadOptions = options.getLoadOptions();

    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public SchemaCrawlerOptions toOptions()
  {
    return new SchemaCrawlerOptions(limitOptions,
                                    filterOptions,
                                    grepOptions,
                                    loadOptions);
  }

  public SchemaCrawlerOptionsBuilder withGrepOptions(final GrepOptions grepOptions)
  {
    if (grepOptions != null)
    {
      this.grepOptions = grepOptions;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withGrepOptionsBuilder(final GrepOptionsBuilder grepOptionsBuilder)
  {
    if (grepOptionsBuilder != null)
    {
      this.grepOptions = grepOptionsBuilder.toOptions();
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withLoadOptions(final LoadOptions loadOptions)
  {
    if (loadOptions != null)
    {
      this.loadOptions = loadOptions;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withLoadOptionsBuilder(final LoadOptionsBuilder loadOptionsBuilder)
  {
    if (loadOptionsBuilder != null)
    {
      this.loadOptions = loadOptionsBuilder.toOptions();
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withFilterOptions(final FilterOptions filterOptions)
  {
    if (filterOptions != null)
    {
      this.filterOptions = filterOptions;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withFilterOptionsBuilder(final FilterOptionsBuilder filterOptionsBuilder)
  {
    if (filterOptionsBuilder != null)
    {
      this.filterOptions = filterOptionsBuilder.toOptions();
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withLimitOptions(final LimitOptions limitOptions)
  {
    if (limitOptions != null)
    {
      this.limitOptions = limitOptions;
    }
    return this;
  }

  public SchemaCrawlerOptionsBuilder withLimitOptionsBuilder(final LimitOptionsBuilder limitOptionsBuilder)
  {
    if (limitOptionsBuilder != null)
    {
      this.limitOptions = limitOptionsBuilder.toOptions();
    }
    return this;
  }

  public LimitOptions getLimitOptions()
  {
    return limitOptions;
  }

  public FilterOptions getFilterOptions()
  {
    return filterOptions;
  }

  public GrepOptions getGrepOptions()
  {
    return grepOptions;
  }

  public LoadOptions getLoadOptions()
  {
    return loadOptions;
  }
}
