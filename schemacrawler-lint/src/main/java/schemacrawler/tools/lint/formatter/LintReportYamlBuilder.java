package schemacrawler.tools.lint.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import schemacrawler.schemacrawler.exceptions.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;

public class LintReportYamlBuilder extends BaseLintReportJacksonBuilder {

  public LintReportYamlBuilder(final OutputOptions outputOptions) throws SchemaCrawlerException {
    super(outputOptions);
  }

  @Override
  protected ObjectMapper newObjectMapper() {
    return new ObjectMapper(new YAMLFactory());
  }
}
