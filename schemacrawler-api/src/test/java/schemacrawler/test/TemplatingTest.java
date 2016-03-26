/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2016, Sualeh Fatehi <sualeh@hotmail.com>.
All rights reserved.
------------------------------------------------------------------------

SchemaCrawler is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

SchemaCrawler and the accompanying materials are made available under
the terms of the Eclipse Public License v1.0, GNU General Public License
v3 or GNU Lesser General Public License v3.

You may elect to redistribute this code under any of these licenses.

The Eclipse Public License is available at:
http://www.eclipse.org/legal/epl-v10.html

The GNU General Public License v3 and the GNU Lesser General Public
License v3 are available at:
http://www.gnu.org/licenses/

========================================================================
*/

package schemacrawler.test;


import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import sf.util.TemplatingUtility;

public class TemplatingTest
{

  @Test
  public void expandTemplate()
    throws Exception
  {
    final Map<String, String> values = new HashMap<>();
    values.put("one", "one.value");
    values.put("another", "two.value");
    values.put("unusual", "10");
    values.put("good", "good.value");
    values.put("split-name", "split-name value");

    String expanded;

    expanded = TemplatingUtility.expandTemplate("No variables", values);
    assertEquals("Incorrect template expansion", "No variables", expanded);

    expanded = TemplatingUtility.expandTemplate("${one} variable", values);
    assertEquals("Incorrect template expansion",
                 "one.value variable",
                 expanded);

    expanded = TemplatingUtility
      .expandTemplate("Has ${one} variable, and ${another} variable", values);
    assertEquals("Incorrect template expansion",
                 "Has one.value variable, and two.value variable",
                 expanded);

    expanded = TemplatingUtility.expandTemplate("Has $${unusual} variable",
                                                values);
    assertEquals("Incorrect template expansion", "Has $10 variable", expanded);

    expanded = TemplatingUtility.expandTemplate("Has ${unusual}} variable",
                                                values);
    assertEquals("Incorrect template expansion", "Has 10} variable", expanded);

    expanded = TemplatingUtility.expandTemplate("Has ${bad variable", values);
    assertEquals("Incorrect template expansion",
                 "Has ${bad variable",
                 expanded);

    expanded = TemplatingUtility
      .expandTemplate("Has ${good} and ${bad variable", values);
    assertEquals("Incorrect template expansion",
                 "Has good.value and ${bad variable",
                 expanded);

    expanded = TemplatingUtility
      .expandTemplate("Has ${bad and ${good} variable", values);
    assertEquals("Incorrect template expansion",
                 "Has ${bad and ${good} variable",
                 expanded);

    expanded = TemplatingUtility.expandTemplate("Has bad} variable", values);
    assertEquals("Incorrect template expansion", "Has bad} variable", expanded);

    expanded = TemplatingUtility.expandTemplate("Has ${undefined} variable",
                                                values);
    assertEquals("Incorrect template expansion",
                 "Has ${undefined} variable",
                 expanded);

    expanded = TemplatingUtility.expandTemplate("Has ${split-name} variable",
                                                values);
    assertEquals("Incorrect template expansion",
                 "Has split-name value variable",
                 expanded);
  }

  @Test
  public void extractTemplateVariables()
    throws Exception
  {
    Set<String> variables;
    List<String> sortedVariables;

    variables = TemplatingUtility.extractTemplateVariables("No variables");
    assertEquals("Incorrect number of variables found", 0, variables.size());

    variables = TemplatingUtility.extractTemplateVariables("${one} variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 1, variables.size());
    assertEquals("Variable not found", "one", sortedVariables.get(0));

    variables = TemplatingUtility
      .extractTemplateVariables("Has ${one} variable, and ${another} variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 2, variables.size());
    assertEquals("Variable not found", "another", sortedVariables.get(0));
    assertEquals("Variable not found", "one", sortedVariables.get(1));

    variables = TemplatingUtility
      .extractTemplateVariables("Has $${unusual} variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 1, variables.size());
    assertEquals("Variable not found", "unusual", sortedVariables.get(0));

    variables = TemplatingUtility
      .extractTemplateVariables("Has ${unusual}} variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 1, variables.size());
    assertEquals("Variable not found", "unusual", sortedVariables.get(0));

    variables = TemplatingUtility
      .extractTemplateVariables("Has ${bad variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 0, variables.size());

    variables = TemplatingUtility
      .extractTemplateVariables("Has ${good} and ${bad variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 1, variables.size());
    assertEquals("Variable not found", "good", sortedVariables.get(0));

    variables = TemplatingUtility
      .extractTemplateVariables("Has ${bad and ${good} variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 1, variables.size());
    assertEquals("Variable not found",
                 "bad and ${good",
                 sortedVariables.get(0));

    variables = TemplatingUtility.extractTemplateVariables("Has bad} variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 0, variables.size());
  }

  private List<String> getSortedVariables(final Set<String> variables)
  {
    final List<String> sortedVariables = new ArrayList<>(variables);
    Collections.sort(sortedVariables);
    return sortedVariables;
  }

}
