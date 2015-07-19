/*
 *
 * SchemaCrawler
 * http://www.schemacrawler.com
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

package schemacrawler.tools.offline;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerCommandLineException;
import schemacrawler.tools.commandline.BaseOptionsParser;
import schemacrawler.tools.options.OutputOptions;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 */
public final class OfflineSnapshotOptionsParser
  extends BaseOptionsParser<OutputOptions>
{

  final OutputOptions options;

  public OfflineSnapshotOptionsParser(final Config config)
  {
    super(config);
    options = new OutputOptions(config);
  }

  @Override
  public OutputOptions getOptions()
    throws SchemaCrawlerCommandLineException
  {
    final String inputSource = config.getStringValue("database", null);
    try
    {
      final Path databaseFile = Paths.get(inputSource);
      options.setCompressedInputFile(databaseFile);
    }
    catch (final IOException e)
    {
      throw new SchemaCrawlerCommandLineException(e.getMessage(), e);
    }

    return options;
  }

}
