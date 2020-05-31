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


public final class LoadOptionsBuilder
  implements OptionsBuilder<LoadOptionsBuilder, LoadOptions>
{

  public static LoadOptionsBuilder builder()
  {
    return new LoadOptionsBuilder();
  }

  public static LoadOptions newLoadOptions()
  {
    return builder().toOptions();
  }

  private boolean isLoadRowCounts;
  private SchemaInfoLevel schemaInfoLevel;

  /**
   * Default options.
   */
  private LoadOptionsBuilder()
  {
    schemaInfoLevel = SchemaInfoLevelBuilder.standard();
  }

  /**
   * Options from properties.
   *
   * @param config
   *   Configuration properties
   */
  @Override
  public LoadOptionsBuilder fromConfig(final Config config)
  {
    if (config == null)
    {
      return this;
    }

    schemaInfoLevel = SchemaInfoLevelBuilder
      .builder()
      .fromConfig(config)
      .toOptions();

    return this;
  }

  @Override
  public LoadOptionsBuilder fromOptions(final LoadOptions options)
  {
    if (options == null)
    {
      return this;
    }

    schemaInfoLevel = options.getSchemaInfoLevel();
    isLoadRowCounts = options.isLoadRowCounts();

    return this;
  }

  @Override
  public Config toConfig()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public LoadOptions toOptions()
  {
    return new LoadOptions(schemaInfoLevel, isLoadRowCounts);
  }

  /**
   * Corresponds to the --load-row-counts command-line argument.
   */
  public final LoadOptionsBuilder loadRowCounts()
  {
    return loadRowCounts(true);
  }

  /**
   * Corresponds to the --load-row-counts=&lt;boolean&gt; command-line
   * argument.
   */
  public final LoadOptionsBuilder loadRowCounts(final boolean value)
  {
    isLoadRowCounts = value;
    return this;
  }

  public LoadOptionsBuilder withSchemaInfoLevel(final SchemaInfoLevel schemaInfoLevel)
  {
    if (schemaInfoLevel != null)
    {
      this.schemaInfoLevel = schemaInfoLevel;
    }
    return this;
  }

  public LoadOptionsBuilder withSchemaInfoLevelBuilder(final SchemaInfoLevelBuilder schemaInfoLevelBuilder)
  {
    if (schemaInfoLevelBuilder != null)
    {
      this.schemaInfoLevel = schemaInfoLevelBuilder.toOptions();
    }
    return this;
  }

  public LoadOptionsBuilder withInfoLevel(final InfoLevel infoLevel)
  {
    if (infoLevel != null)
    {
      this.schemaInfoLevel = infoLevel.toSchemaInfoLevel();
    }
    return this;
  }

}
