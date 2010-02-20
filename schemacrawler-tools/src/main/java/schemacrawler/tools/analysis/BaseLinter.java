package schemacrawler.tools.analysis;


import java.util.ArrayList;
import java.util.List;

import schemacrawler.schema.DatabaseObject;

abstract class BaseLinter<D extends DatabaseObject>
  implements Linter<D>
{

  protected final void addLint(final D databaseObject, final Lint lint)
  {
    final List<Lint> lints = databaseObject.getAttribute(Lint.LINT_KEY,
                                                         new ArrayList<Lint>());
    if (lint != null)
    {
      lints.add(lint);
      databaseObject.setAttribute(Lint.LINT_KEY, lints);
    }
  }

}
