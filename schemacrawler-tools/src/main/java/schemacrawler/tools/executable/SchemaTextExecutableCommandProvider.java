package schemacrawler.tools.executable;


import schemacrawler.schemacrawler.SchemaCrawlerException;

class SchemaTextExecutableCommandProvider
  extends ExecutableCommandProvider
{

  public SchemaTextExecutableCommandProvider(String command,
                                             String executableClassName)
  {
    super(command, executableClassName);
  }

  @Override
  public Executable newExecutable()
    throws SchemaCrawlerException
  {
    final Executable executable = super.newExecutable();
    executable.setAdditionalConfiguration(null);
    return executable;
  }

}
