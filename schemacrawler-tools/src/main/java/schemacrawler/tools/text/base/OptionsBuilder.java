package schemacrawler.tools.text.base;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Options;

public interface OptionsBuilder<O extends Options>
{

  OptionsBuilder<O> setFromConfig(Config config);

  Config toConfig();

  O toOptions();

}
