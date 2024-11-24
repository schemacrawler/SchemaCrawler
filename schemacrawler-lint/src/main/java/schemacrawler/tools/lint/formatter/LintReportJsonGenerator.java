package schemacrawler.tools.lint.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;

import schemacrawler.tools.options.OutputOptions;

public class LintReportJsonGenerator extends BaseLintReportJacksonGenerator {

  public LintReportJsonGenerator(final OutputOptions outputOptions) {
    super(outputOptions);
  }

  @Override
  protected ObjectMapper newObjectMapper() {
    return new ObjectMapper();
  }
}
