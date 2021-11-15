package schemacrawler.tools.lint.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import schemacrawler.tools.options.OutputOptions;

public class LintReportYamlBuilder extends BaseLintReportJacksonBuilder {

  public LintReportYamlBuilder(final OutputOptions outputOptions) {
    super(outputOptions);
  }

  @Override
  protected ObjectMapper newObjectMapper() {
    return new ObjectMapper(new YAMLFactory());
  }
}
