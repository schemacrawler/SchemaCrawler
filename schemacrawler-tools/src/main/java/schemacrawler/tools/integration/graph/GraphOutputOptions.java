package schemacrawler.tools.integration.graph;


import java.io.File;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.UUID;

import schemacrawler.tools.options.OutputOptions;

public class GraphOutputOptions
  extends OutputOptions
{

  private static final long serialVersionUID = -7333423775806155323L;

  private final OutputOptions outputOptions;

  public GraphOutputOptions(final OutputOptions outputOptions)
  {
    this.outputOptions = outputOptions;
  }

  /**
   * Gets the diagram file. If no output filename is provided, then a
   * default name is constructed using the file extension.
   *
   * @return Diagram file
   */
  public File getDiagramFile()
  {
    final File diagramOutputFile = getOutputFile();
    final File diagramFile;
    if (diagramOutputFile == null)
    {
      diagramFile = new File(".", "schemacrawler." + UUID.randomUUID() + "."
                                  + getOutputFormat().getFormat());
    }
    else
    {
      diagramFile = diagramOutputFile;
    }
    return diagramFile;
  }

  @Override
  public Charset getInputCharset()
  {
    return outputOptions.getInputCharset();
  }

  @Override
  public Charset getOutputCharset()
  {
    return outputOptions.getOutputCharset();
  }

  @Override
  public File getOutputFile()
  {
    return outputOptions.getOutputFile();
  }

  @Override
  public GraphOutputFormat getOutputFormat()
  {
    final GraphOutputFormat graphOutputFormat;
    if (!outputOptions.hasOutputFormat())
    {
      graphOutputFormat = GraphOutputFormat.fromFormat(getOutputFormatValue());
    }
    else
    {
      graphOutputFormat = GraphOutputFormat.png;
    }
    return graphOutputFormat;
  }

  @Override
  public String getOutputFormatValue()
  {
    return outputOptions.getOutputFormatValue();
  }

  @Override
  public Writer getWriter()
  {
    return outputOptions.getWriter();
  }

  @Override
  public boolean hasOutputFormat()
  {
    return GraphOutputFormat.isGraphOutputFormat(outputOptions
      .getOutputFormatValue());
  }

  @Override
  public boolean isConsoleOutput()
  {
    return outputOptions.isConsoleOutput();
  }

  @Override
  public boolean isFileOutput()
  {
    return outputOptions.isFileOutput();
  }

  @Override
  public void setInputEncoding(final String inputEncoding)
  {
    outputOptions.setInputEncoding(inputEncoding);
  }

  @Override
  public void setOutputEncoding(final String outputEncoding)
  {
    outputOptions.setOutputEncoding(outputEncoding);
  }

  @Override
  public void setOutputFile(final File outputFile)
  {
    outputOptions.setOutputFile(outputFile);
  }

  @Override
  public void setOutputFormatValue(final String outputFormatValue)
  {
    outputOptions.setOutputFormatValue(outputFormatValue);
  }

  @Override
  public void setWriter(final Writer writer)
  {
    outputOptions.setWriter(writer);
  }

  @Override
  public String toString()
  {
    return outputOptions.toString();
  }

}
