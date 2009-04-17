/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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

package schemacrawler.tools.integration.graph;


import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import schemacrawler.tools.OutputFormat;
import schemacrawler.tools.OutputOptions;
import schemacrawler.tools.integration.SchemaExecutable;
import schemacrawler.tools.schematext.SchemaTextOptions;
import schemacrawler.tools.util.HtmlFormattingHelper;
import sf.util.Utilities;

/**
 * Main executor for the graphing integration.
 * 
 * @author Sualeh Fatehi
 */
public final class GraphExecutable
  extends SchemaExecutable
{

  private static final Logger LOGGER = Logger.getLogger(GraphExecutable.class
    .getName());

  private static String dotError()
  {
    final byte[] text = Utilities.readFully(HtmlFormattingHelper.class
      .getResourceAsStream("/dot.error.txt"));
    return new String(text);
  }

  /**
   * Get connection parameters, and creates a connection, and crawls the
   * schema.
   * 
   * @param args
   *        Arguments passed into the program from the command line.
   * @throws Exception
   *         On an exception
   */
  public void main(final String[] args)
    throws Exception
  {
    executeOnSchema(args, "/schemacrawler-dot-readme.txt");
  }

  @Override
  protected void doExecute(final DataSource dataSource)
    throws Exception
  {
    final OutputOptions outputOptions = toolOptions.getOutputOptions();
    final File outputFile = outputOptions.getOutputFile();

    try
    {
      final String outputFormat = outputOptions.getOutputFormatValue();
      if (outputFormat.equalsIgnoreCase("dot"))
      {
        writeDotFile(dataSource, Utilities.changeFileExtension(outputFile,
                                                               ".dot"));
      }
      else
      {
        final File dotFile = File.createTempFile("schemacrawler.", ".dot");
        dotFile.deleteOnExit();

        final GraphGenerator dot = new GraphGenerator();
        writeDotFile(dataSource, dotFile);
        dot.generateDiagram(dotFile, outputFormat, outputFile);
      }
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.WARNING, "Could not write diagram", e);
      writeDotFile(dataSource, Utilities
        .changeFileExtension(outputFile, ".dot"));
      System.out.println(dotError());
    }
  }

  private void writeDotFile(final DataSource dataSource, final File dotFile)
  {
    try
    {
      final OutputOptions outputOptions = new OutputOptions();
      outputOptions.setOutputFormatValue(OutputFormat.dot.name());
      outputOptions.setOutputFileName(dotFile.getAbsolutePath());

      toolOptions = new SchemaTextOptions();
      toolOptions.setOutputOptions(outputOptions);
      execute(dataSource);
    }
    catch (final Exception e)
    {
      LOGGER.log(Level.SEVERE, "Could not write diagram, " + dotFile, e);
    }
  }

}
