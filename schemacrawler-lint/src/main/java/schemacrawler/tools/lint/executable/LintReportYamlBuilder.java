package schemacrawler.tools.lint.executable;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import schemacrawler.schemacrawler.SchemaCrawlerException;
import schemacrawler.tools.options.OutputOptions;

public class LintReportYamlBuilder
  extends BaseLintReportJacksonBuilder
{

  LintReportYamlBuilder(final OutputOptions outputOptions)
    throws SchemaCrawlerException
  {
    super(outputOptions);
  }

  @Override
  protected ObjectMapper newObjectMapper()
  {
    return new ObjectMapper(new YAMLFactory());
  }

}
