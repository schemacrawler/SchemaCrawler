/*
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2014, Sualeh Fatehi.
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

package schemacrawler.tools.commandline;


import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Options;
import schemacrawler.schemacrawler.SchemaCrawlerException;

/**
 * Parses the command-line.
 *
 * @author Sualeh Fatehi
 * @param <O>
 *        Options to be parsed from the command-line.
 */
public abstract class BaseOptionsParser<O extends Options>
{

  protected final Config config;

  protected BaseOptionsParser(final Config config)
  {
    this.config = requireNonNull(config);
  }

  protected final void consumeOption(final String primaryOptionName)
  {
    config.remove(primaryOptionName);
  }

  protected abstract O getOptions()
    throws SchemaCrawlerException;

  protected final void normalizeOptionName(final String primaryOptionName,
                                           final String... alternateOptionName)
  {
    requireNonNull(primaryOptionName);
    final List<String> optionNames = new ArrayList<>();
    optionNames.add(primaryOptionName);
    if (alternateOptionName != null)
    {
      optionNames.addAll(Arrays.asList(alternateOptionName));
    }
    Collections.reverse(optionNames);

    String value = null;
    boolean foundValue = false;
    for (final String optionName: optionNames)
    {
      if (config.hasValue(optionName))
      {
        value = config.get(optionName);
        foundValue = true;
      }
      config.remove(optionName);
    }

    if (foundValue)
    {
      config.put(primaryOptionName, value);
    }
  }

}
