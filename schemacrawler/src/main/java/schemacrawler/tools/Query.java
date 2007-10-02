package schemacrawler.tools;


import java.io.Serializable;
import java.util.Set;

import sf.util.Utilities;

public class Query
  implements Serializable
{

  private final String name;
  private final String query;

  public Query(String name, String query)
  {
    this.name = name;
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
