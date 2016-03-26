/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi.
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
