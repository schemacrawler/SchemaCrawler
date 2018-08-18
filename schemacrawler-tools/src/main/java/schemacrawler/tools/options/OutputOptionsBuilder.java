package schemacrawler.tools.options;


import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.iosource.ClasspathInputResource;
import schemacrawler.tools.iosource.CompressedFileInputResource;
import schemacrawler.tools.iosource.CompressedFileOutputResource;
import schemacrawler.tools.iosource.ConsoleOutputResource;
import schemacrawler.tools.iosource.EmptyInputResource;
import schemacrawler.tools.iosource.FileInputResource;
import schemacrawler.tools.iosource.FileOutputResource;
import schemacrawler.tools.iosource.InputResource;
import schemacrawler.tools.iosource.OutputResource;
import schemacrawler.tools.iosource.WriterOutputResource;

public final class OutputOptionsBuilder
  implements OptionsBuilder<OutputOptionsBuilder, OutputOptions>
{

  private static final String SCHEMACRAWLER_DATA = "schemacrawler.data";

  private static final String SC_INPUT_ENCODING = "schemacrawler.encoding.input";
  private static final String SC_OUTPUT_ENCODING = "schemacrawler.encoding.output";

  public static OutputOptionsBuilder builder()
  {
    return new OutputOptionsBuilder();
  }

  public static OutputOptionsBuilder builder(final OutputOptions outputOptions)
  {
    return new OutputOptionsBuilder().fromOptions(outputOptions);
  }

  public static OutputOptions newOutputOptions()
  {
    return new OutputOptionsBuilder().toOptions();
  }

  public static OutputOptions newOutputOptions(final Config config)
  {
    return new OutputOptionsBuilder().fromConfig(config).toOptions();
  }

  public static OutputOptions newOutputOptions(final OutputFormat outputFormat,
                                               final Path outputFile)
  {
    return new OutputOptionsBuilder().withOutputFormat(outputFormat)
      .withOutputFile(outputFile).toOptions();
  }

  public static OutputOptions newOutputOptions(final OutputFormat outputFormat,
                                               final Writer writer)
  {
    return new OutputOptionsBuilder().withOutputFormat(outputFormat)
      .withOutputWriter(writer).toOptions();
  }

  public static OutputOptions newOutputOptions(final String outputFormatValue,
                                               final Path outputFile)
  {
    return new OutputOptionsBuilder().withOutputFormatValue(outputFormatValue)
      .withOutputFile(outputFile).toOptions();
  }

  public static OutputOptions newOutputOptions(final String outputFormatValue,
                                               final Writer writer)
  {
    return new OutputOptionsBuilder().withOutputFormatValue(outputFormatValue)
      .withOutputWriter(writer).toOptions();
  }

  private OutputResource outputResource;
  private InputResource inputResource;
  private String outputFormatValue;
  private Charset inputEncodingCharset;
  private Charset outputEncodingCharset;

  private OutputOptionsBuilder()
  {
    // Default values are set at the time of building options
    // All values are set to null, and corrected at the time of
    // converting to options
  }

  @Override
  public OutputOptionsBuilder fromConfig(final Config config)
  {
    final Config configProperties;
    if (config == null)
    {
      configProperties = new Config();
    }
    else
    {
      configProperties = config;
    }

    this
      .withInputEncoding(configProperties.getStringValue(SC_INPUT_ENCODING,
                                                         UTF_8.name()))
      .withOutputEncoding(configProperties.getStringValue(SC_OUTPUT_ENCODING,
                                                          UTF_8.name()));

    return this;
  }

  @Override
  public OutputOptionsBuilder fromOptions(final OutputOptions options)
  {
    if (options == null)
    {
      return this;
    }

    this.withInputEncoding(options.getInputCharset())
      .withOutputEncoding(options.getOutputCharset())
      .withOutputFormatValue(options.getOutputFormatValue());
    inputResource = options.getInputResource();
    outputResource = options.getOutputResource();

    return this;
  }

  @Override
  public Config toConfig()
  {
    final Config config = new Config();
    config.setStringValue(SC_INPUT_ENCODING, inputEncodingCharset.name());
    config.setStringValue(SC_OUTPUT_ENCODING, outputEncodingCharset.name());
    return config;
  }

  @Override
  public OutputOptions toOptions()
  {
    // Set any values that have not been explicitly set to defaults
    if (inputResource == null)
    {
      withInputResource(createInputResource(outputFormatValue));
    }
    withInputEncoding(inputEncodingCharset);
    withOutputResource(outputResource);
    withOutputEncoding(inputEncodingCharset);
    if (outputFormatValue == null)
    {
      outputFormatValue = TextOutputFormat.text.name();
    }

    return new OutputOptions(inputResource,
                             inputEncodingCharset,
                             outputResource,
                             outputEncodingCharset,
                             outputFormatValue);
  }

  /**
   * Sets the name of the input file for compressed input. It is
   * important to note that the input encoding should be available at
   * this point.
   *
   * @param inputFile
   *        Path to compressed file.
   * @throws IOException
   *         When file cannot be read
   * @return Builder
   */
  public OutputOptionsBuilder withCompressedInputFile(final Path inputFile)
    throws IOException
  {
    requireNonNull(inputFile, "No input file provided");
    inputResource = new CompressedFileInputResource(inputFile,
                                                    SCHEMACRAWLER_DATA);
    return this;
  }

  /**
   * Sets the name of the output file for compressed output. It is
   * important to note that the output encoding should be available at
   * this point.
   *
   * @param outputFile
   *        Output path.
   * @throws IOException
   * @return Builder
   */
  public OutputOptionsBuilder withCompressedOutputFile(final Path outputFile)
    throws IOException
  {
    requireNonNull(outputFile, "No output file provided");
    outputResource = new CompressedFileOutputResource(outputFile,
                                                      SCHEMACRAWLER_DATA);
    return this;
  }

  public OutputOptionsBuilder withConsoleOutput()
  {
    outputResource = new ConsoleOutputResource();
    return this;
  }

  public OutputOptionsBuilder withInputEncoding(final Charset inputCharset)
  {
    if (inputCharset == null)
    {
      inputEncodingCharset = UTF_8;
    }
    else
    {
      inputEncodingCharset = inputCharset;
    }
    return this;
  }

  /**
   * Set character encoding for input files, such as scripts and
   * templates.
   *
   * @param inputEncoding
   *        Input encoding
   * @return Builder
   */
  public OutputOptionsBuilder withInputEncoding(final String inputEncoding)
  {
    try
    {
      inputEncodingCharset = Charset.forName(inputEncoding);
    }
    catch (final IllegalArgumentException e)
    {
      inputEncodingCharset = UTF_8;
    }
    return this;
  }

  /**
   * Sets the name of the input file. It is important to note that the
   * input encoding should be available at this point.
   *
   * @param inputFile
   *        Input path.
   * @throws IOException
   *         When file cannot be read
   * @return Builder
   */
  public OutputOptionsBuilder withInputFile(final Path inputFile)
    throws IOException
  {
    requireNonNull(inputFile, "No input file provided");
    inputResource = new FileInputResource(inputFile);
    return this;
  }

  public OutputOptionsBuilder withInputResource(final InputResource inputResource)
  {
    if (inputResource == null)
    {
      this.inputResource = new EmptyInputResource();
    }
    else
    {
      this.inputResource = inputResource;
    }
    return this;
  }

  /**
   * Sets the name of the input resource, first from a file, failing
   * which from the classpath. It is important to note that the input
   * encoding should be available at this point.
   *
   * @param inputResourceName
   *        Input resource name, which could be a file path, or a
   *        classpath resource.
   * @return Builder
   */
  public OutputOptionsBuilder withInputResourceName(final String inputResourceName)
  {
    requireNonNull(inputResourceName, "No input resource name provided");
    inputResource = createInputResource(inputResourceName);
    return this;
  }

  public OutputOptionsBuilder withOutputEncoding(final Charset outputCharset)
  {
    if (outputCharset == null)
    {
      outputEncodingCharset = UTF_8;
    }
    else
    {
      outputEncodingCharset = outputCharset;
    }
    return this;
  }

  /**
   * Set character encoding for output files.
   *
   * @param outputEncoding
   *        Output encoding
   * @return Builder
   */
  public OutputOptionsBuilder withOutputEncoding(final String outputEncoding)
  {
    try
    {
      outputEncodingCharset = Charset.forName(outputEncoding);
    }
    catch (final IllegalArgumentException e)
    {
      outputEncodingCharset = UTF_8;
    }
    return this;
  }

  /**
   * Sets the name of the output file. It is important to note that the
   * output encoding should be available at this point.
   *
   * @param outputFile
   *        Output path.
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFile(final Path outputFile)
  {
    requireNonNull(outputFile, "No output file provided");
    outputResource = new FileOutputResource(outputFile);
    return this;
  }

  /**
   * Sets output format.
   *
   * @param outputFormat
   *        Output format
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFormat(final OutputFormat outputFormat)
  {
    outputFormatValue = requireNonNull(outputFormat,
                                       "No output format provided").getFormat();
    return this;
  }

  /**
   * Sets output format value.
   *
   * @param outputFormatValue
   *        Output format value
   * @return Builder
   */
  public OutputOptionsBuilder withOutputFormatValue(final String outputFormatValue)
  {
    this.outputFormatValue = requireNonNull(outputFormatValue,
                                            "No output format value provided");
    return this;
  }

  public OutputOptionsBuilder withOutputResource(final OutputResource outputResource)
  {
    if (outputResource == null)
    {
      if (outputFormatValue == null
          || TextOutputFormat.text.name().equals(outputFormatValue))
      {
        this.outputResource = new ConsoleOutputResource();
      }
      else if (!(inputResource instanceof EmptyInputResource))
      {
        // Tacky hack for script
        this.outputResource = new ConsoleOutputResource();
      }
      else
      {
        final String extension;
        if ("htmlx".equals(outputFormatValue))
        {
          // Tacky hack for htmlx format
          extension = "svg.html";
        }
        else
        {
          extension = outputFormatValue;
        }

        final Path randomOutputFile = Paths
          .get(".",
               String
                 .format("schemacrawler-%s.%s", UUID.randomUUID(), extension))
          .normalize().toAbsolutePath();
        this.outputResource = new FileOutputResource(randomOutputFile);
      }
    }
    else
    {
      this.outputResource = outputResource;
    }
    return this;
  }

  public OutputOptionsBuilder withOutputWriter(final Writer writer)
  {
    requireNonNull(writer, "No output writer provided");
    outputResource = new WriterOutputResource(writer);
    return this;
  }

  private InputResource createInputResource(final String inputResourceName)
  {
    InputResource inputResource = null;
    try
    {
      final Path filePath = Paths.get(inputResourceName);
      inputResource = new FileInputResource(filePath);
    }
    catch (final Exception e)
    {
      // No-op
    }
    try
    {
      if (inputResource == null)
      {
        inputResource = new ClasspathInputResource(inputResourceName);
      }
    }
    catch (final Exception e)
    {
      // No-op
    }
    if (inputResource == null)
    {
      inputResource = new EmptyInputResource();
    }
    return inputResource;
  }

}
