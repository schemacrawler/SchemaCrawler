/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import schemacrawler.schemacrawler.InclusionRule;
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
  private InclusionRule tableColumnInclusionRule;
  private InclusionRule procedureInclusionRule;
  private InclusionRule procedureColumnInclusionRule;
  private InclusionRule definitionTextInclusionRule;
  private boolean invertMatch;

  /**
   * Creates default options.
   */
  public GrepOptions()
  {
    tableInclusionRule = new InclusionRule();
    tableColumnInclusionRule = new InclusionRule();

    procedureInclusionRule = new InclusionRule();
    procedureColumnInclusionRule = new InclusionRule();

    definitionTextInclusionRule = new InclusionRule();

    invertMatch = false;
  }

  /**
   * Gets the inclusion rule for text contained in a definition.
   * 
   * @return Inclusion rule for text contained in a definition.
   */
  public InclusionRule getDefinitionTextInclusionRule()
  {
    return definitionTextInclusionRule;
  }

  /**
   * Gets the column inclusion rule.
   * 
   * @return Column inclusion rule.
   */
  public InclusionRule getProcedureColumnInclusionRule()
  {
    return procedureColumnInclusionRule;
  }

  /**
   * Gets the procedure inclusion rule.
   * 
   * @return Procedure inclusion rule.
   */
  public InclusionRule getProcedureInclusionRule()
  {
    return procedureInclusionRule;
  }

  /**
   * Gets the column inclusion rule.
   * 
   * @return Column inclusion rule.
   */
  public InclusionRule getTableColumnInclusionRule()
  {
    return tableColumnInclusionRule;
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
   * Sets the inclusion rule for text contained in a definition.
   * 
   * @param definitionTextInclusionRule
   *        Inclusion rule for text contained in a definition.
   */
  public void setDefinitionTextInclusionRule(final InclusionRule definitionTextInclusionRule)
  {
    if (definitionTextInclusionRule == null)
    {
      this.definitionTextInclusionRule = new InclusionRule();
    }
    else
    {
      this.definitionTextInclusionRule = definitionTextInclusionRule;
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
   * Sets the column inclusion rule.
   * 
   * @param procedureColumnInclusionRule
   *        Column inclusion rule.
   */
  public void setProcedureColumnInclusionRule(final InclusionRule procedureColumnInclusionRule)
  {
    if (procedureColumnInclusionRule == null)
    {
      this.procedureColumnInclusionRule = new InclusionRule();
    }
    else
    {
      this.procedureColumnInclusionRule = procedureColumnInclusionRule;
    }
  }

  /**
   * Sets the procedure inclusion rule.
   * 
   * @param procedureInclusionRule
   *        Procedure inclusion rule.
   */
  public void setProcedureInclusionRule(final InclusionRule procedureInclusionRule)
  {
    if (procedureInclusionRule == null)
    {
      this.procedureInclusionRule = new InclusionRule();
    }
    else
    {
      this.procedureInclusionRule = procedureInclusionRule;
    }
  }

  /**
   * Sets the column inclusion rule.
   * 
   * @param tableColumnInclusionRule
   *        Column inclusion rule.
   */
  public void setTableColumnInclusionRule(final InclusionRule tableColumnInclusionRule)
  {
    if (tableColumnInclusionRule == null)
    {
      this.tableColumnInclusionRule = new InclusionRule();
    }
    else
    {
      this.tableColumnInclusionRule = tableColumnInclusionRule;
    }
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
