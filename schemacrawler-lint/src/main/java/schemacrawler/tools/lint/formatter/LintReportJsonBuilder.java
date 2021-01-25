package schemacrawler.tools.lint.formatter;

import com.fasterxml.jackson.databind.ObjectMapper;

import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;

public class LintReportJsonBuilder extends BaseLintReportJacksonBuilder {

  public LintReportJsonBuilder(final OutputOptions outputOptions) throws SchemaCrawlerException {
    super(outputOptions);
  }

  @Override
  protected ObjectMapper newObjectMapper() {
    return new ObjectMapper();
  }
}
