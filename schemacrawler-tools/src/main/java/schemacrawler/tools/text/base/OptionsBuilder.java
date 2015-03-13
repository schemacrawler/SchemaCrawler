package schemacrawler.tools.text.base;


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.Options;

public interface OptionsBuilder<O extends Options>
{

  Config toConfig();

  OptionsBuilder<O> setFromConfig(Config config);

  O toOptions();

}
