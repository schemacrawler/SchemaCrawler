/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.text.diagram;

import static java.util.Objects.requireNonNull;
import static us.fatehi.utility.IOUtility.isFileReadable;
import static us.fatehi.utility.IOUtility.isFileWritable;

import java.nio.file.Path;

import schemacrawler.schemacrawler.exceptions.IORuntimeException;
import schemacrawler.tools.command.text.diagram.options.DiagramOutputFormat;

abstract class AbstractGraphProcessExecutor implements GraphExecutor {

  protected final Path dotFile;
  protected final Path outputFile;
  protected final DiagramOutputFormat diagramOutputFormat;

  protected AbstractGraphProcessExecutor(
      final Path dotFile, final Path outputFile, final DiagramOutputFormat diagramOutputFormat) {
    requireNonNull(dotFile, "No DOT file provided");
    requireNonNull(outputFile, "No diagram output file provided");
    requireNonNull(diagramOutputFormat, "No diagram output format provided");

    this.dotFile = dotFile.normalize().toAbsolutePath();
    this.outputFile = outputFile.normalize().toAbsolutePath();
    this.diagramOutputFormat = diagramOutputFormat;

    if (!isFileReadable(this.dotFile)) {
      throw new IORuntimeException(String.format("Cannot read DOT file <%s>", this.dotFile));
    }

    if (!isFileWritable(this.outputFile)) {
      throw new IORuntimeException(String.format("Cannot write output file <%s>", this.outputFile));
    }
  }
}
