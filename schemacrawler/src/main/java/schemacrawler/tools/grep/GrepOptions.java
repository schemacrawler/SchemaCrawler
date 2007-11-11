/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2007, Sualeh Fatehi.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 */
package schemacrawler.tools.grep;


import schemacrawler.crawl.InclusionRule;
import schemacrawler.tools.schematext.SchemaTextOptions;

/**
 * Additional options needed for grep.
 * 
 * @author Sualeh Fatehi
 */
public class GrepOptions
  extends SchemaTextOptions
{

  private static final long serialVersionUID = -1606027815351884928L;

  private InclusionRule tableInclusionRule;
  private InclusionRule columnInclusionRule;
  private boolean invertMatch;

  /**
   * Creates default options.
   */
  public GrepOptions()
  {
    tableInclusionRule = new InclusionRule();
    columnInclusionRule = new InclusionRule();

    invertMatch = false;
  }

  /**
   * Gets the column inclusion rule.
   * 
   * @return Column inclusion rule.
   */
  public InclusionRule getColumnInclusionRule()
  {
    return columnInclusionRule;
  }

  /**
   * Gets the table inclusion rule.
   * 
   * @return Table inclusion rule.
   */
  public InclusionRule getTableInclusionRule()
  {
    return tableInclusionRule;
  }

  /**
   * Whether to invert matches.
   * 
   * @return Whether to invert matches.
   */
  public boolean isInvertMatch()
  {
    return invertMatch;
  }

  /**
   * Sets the column inclusion rule.
   * 
   * @param columnInclusionRule
   *        Column inclusion rule.
   */
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

  /**
   * Set whether to invert matches.
   * 
   * @param invertMatch
   *        Whether to invert matches.
   */
  public void setInvertMatch(final boolean invertMatch)
  {
    this.invertMatch = invertMatch;
  }

  /**
   * Sets the table inclusion rule.
   * 
   * @param tableInclusionRule
   *        Table inclusion rule.
   */
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
