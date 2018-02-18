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
package schemacrawler.tools.integration.graph;


import static java.util.Objects.requireNonNull;
import static sf.util.IOUtility.isFileReadable;
import static sf.util.IOUtility.isFileWritable;

import java.nio.file.Path;

import schemacrawler.schemacrawler.SchemaCrawlerException;

abstract class AbstractGraphProcessExecutor
  implements GraphExecutor
{

  protected final Path dotFile;
  protected final Path outputFile;
  protected final GraphOutputFormat graphOutputFormat;

  protected AbstractGraphProcessExecutor(final Path dotFile,
                                         final Path outputFile,
                                         final GraphOutputFormat graphOutputFormat)
    throws SchemaCrawlerException
  {
    requireNonNull(dotFile, "No DOT file provided");
    requireNonNull(outputFile, "No graph output file provided");
    requireNonNull(graphOutputFormat, "No graph output format provided");

    this.dotFile = dotFile.normalize().toAbsolutePath();
    this.outputFile = outputFile.normalize().toAbsolutePath();
    this.graphOutputFormat = graphOutputFormat;

    if (!isFileReadable(this.dotFile))
    {
      throw new SchemaCrawlerException("Cannot read DOT file, " + this.dotFile);
    }

    if (!isFileWritable(this.outputFile))
    {
      throw new SchemaCrawlerException("Cannot write output file, "
                                       + this.outputFile);
    }
  }

}
