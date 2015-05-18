package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Procedure;

class ProcedureReference
  extends DatabaseObjectReference<Procedure>
{

  private static final long serialVersionUID = 5422838457822334919L;

  ProcedureReference(final Procedure procedure)
  {
    super(requireNonNull(procedure, "No procedure provided"),
          new ProcedurePartial(procedure));
  }

}
