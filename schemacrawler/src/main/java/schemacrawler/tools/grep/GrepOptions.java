package schemacrawler.tools.grep;


import schemacrawler.crawl.InclusionRule;
import schemacrawler.tools.schematext.SchemaTextOptions;

public class GrepOptions
  extends SchemaTextOptions
{

  private static final long serialVersionUID = -1606027815351884928L;

  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;
  private boolean invertMatch;

  public GrepOptions()
  {
    tableInclusionRule = new InclusionRule();
    columnInclusionRule = new InclusionRule();

    invertMatch = false;
  }

  public InclusionRule getColumnInclusionRule()
  {
    return columnInclusionRule;
  }

  public InclusionRule getTableInclusionRule()
  {
    return tableInclusionRule;
  }

  public boolean isInvertMatch()
  {
    return invertMatch;
  }

  public void setColumnInclusionRule(final InclusionRule columnInclusionRule)
  {
    if (columnInclusionRule == null)
    {
      this.columnInclusionRule = new InclusionRule();
    }
    else
    {
      this.columnInclusionRule = columnInclusionRule;
    }
  }

  public void setInvertMatch(final boolean invertMatch)
  {
    this.invertMatch = invertMatch;
  }

  public void setTableInclusionRule(final InclusionRule tableInclusionRule)
  {
    if (tableInclusionRule == null)
    {
      this.tableInclusionRule = new InclusionRule();
    }
    else
    {
      this.tableInclusionRule = tableInclusionRule;
    }
  }

}
