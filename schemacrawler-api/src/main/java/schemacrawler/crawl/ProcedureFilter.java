/* 
 *
 * SchemaCrawler
 * http://sourceforge.net/projects/schemacrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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
package schemacrawler.crawl;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import schemacrawler.schema.Procedure;
import schemacrawler.schema.ProcedureColumn;
import schemacrawler.schemacrawler.InclusionRule;
import schemacrawler.schemacrawler.SchemaCrawlerOptions;

class ProcedureFilter
{

  private static final Logger LOGGER = Logger.getLogger(ProcedureFilter.class
    .getName());

  private final SchemaCrawlerOptions options;
  private final NamedObjectList<MutableProcedure> allProcedures;

  public ProcedureFilter(final SchemaCrawlerOptions options,
                         final NamedObjectList<MutableProcedure> allProcedures)
  {
    this.options = options;
    this.allProcedures = allProcedures;
  }

  public void filter()
  {
    final Collection<MutableProcedure> filteredProcedures = doFilter();
    for (final MutableProcedure procedure: allProcedures)
    {
      if (!filteredProcedures.contains(procedure))
      {
        ((MutableSchema) procedure.getSchema()).removeProcedure(procedure);
        allProcedures.remove(procedure);
      }
    }
  }

  private Collection<MutableProcedure> doFilter()
  {
    // Filter for grep
    final Set<MutableProcedure> greppedProcedures = new HashSet<MutableProcedure>();
    for (final MutableProcedure procedure: allProcedures)
    {
      if (grepMatch(options, procedure))
      {
        greppedProcedures.add(procedure);
      }
    }

    return greppedProcedures;
  }

  /**
   * Special case for "grep" like functionality. Handle procedure if a
   * procedure column inclusion rule is found, and at least one column
   * matches the rule.
   * 
   * @param options
   *        Options
   * @param procedure
   *        Procedure to check
   * @return Whether the column should be included
   */
  private boolean grepMatch(final SchemaCrawlerOptions options,
                            final Procedure procedure)
  {
    final boolean invertMatch = options.isGrepInvertMatch();
    final boolean checkIncludeForColumns = options.isGrepProcedureColumns();
    final boolean checkIncludeForDefinitions = options.isGrepDefinitions();

    final InclusionRule grepProcedureColumnInclusionRule = options
      .getGrepProcedureColumnInclusionRule();
    final InclusionRule grepDefinitionInclusionRule = options
      .getGrepDefinitionInclusionRule();

    if (!checkIncludeForColumns && !checkIncludeForDefinitions)
    {
      return true;
    }

    boolean includeForColumns = false;
    boolean includeForDefinitions = false;
    final ProcedureColumn[] columns = procedure.getColumns();
    for (final ProcedureColumn column: columns)
    {
      if (checkIncludeForColumns)
      {
        if (grepProcedureColumnInclusionRule.include(column.getFullName()))
        {
          includeForColumns = true;
          break;
        }
      }
    }
    // Additional include checks for definitions
    if (checkIncludeForDefinitions)
    {
      if (grepDefinitionInclusionRule.include(procedure.getRemarks()))
      {
        includeForDefinitions = true;
      }
      if (grepDefinitionInclusionRule.include(procedure.getDefinition()))
      {
        includeForDefinitions = true;
      }
    }

    boolean include = includeForColumns || includeForDefinitions;
    if (invertMatch)
    {
      include = !include;
    }

    if (!include)
    {
      LOGGER.log(Level.FINE, "Removing procedure " + procedure
                             + " since it does not match the grep pattern");
    }

    return include;
  }

}
