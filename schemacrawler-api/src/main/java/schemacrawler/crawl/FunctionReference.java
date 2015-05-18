package schemacrawler.crawl;


import static java.util.Objects.requireNonNull;
import schemacrawler.schema.Function;

class FunctionReference
  extends DatabaseObjectReference<Function>
{

  private static final long serialVersionUID = -5166020646865781875L;

  FunctionReference(final Function function)
  {
    super(requireNonNull(function, "No function provided"),
          new FunctionPartial(function));
  }

}
