package schemacrawler.tools;


import java.io.Serializable;
import java.util.Set;

import sf.util.Utilities;

public class Query
  implements Serializable
{

  private static final long serialVersionUID = 2820769346069413473L;

  private final String name;
  private final String query;

  public Query(final String name, final String query)
  {
    if (name == null || name.length() == 0)
    {
      throw new IllegalArgumentException("No name provided for the query");
    }
    this.name = name;
    if (query == null || query.length() == 0)
    {
      throw new IllegalArgumentException("No SQL provided for query " + name);
    }
    this.query = query;
  }

  /**
   * @return the name
   */
  public String getName()
  {
    return name;
  }

  /**
   * @return the query
   */
  public String getQuery()
  {
    return query;
  }

  public boolean isQueryOver()
  {
    boolean isQueryOver = false;
    final Set<String> keys = Utilities.extractTemplateVariables(query);
    final String[] queryOverKeys = {
        "table", "table_type"
    };
    for (final String element: queryOverKeys)
    {
      if (keys.contains(element))
      {
        isQueryOver = true;
        break;
      }
    }
    return isQueryOver;
  }

}
