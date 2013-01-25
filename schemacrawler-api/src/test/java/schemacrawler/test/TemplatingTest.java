/*
 * SchemaCrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
    final Map<String, String> values = new HashMap<String, String>();
    values.put("one", "one.value");
    values.put("another", "two.value");
    values.put("unusual", "10");
    values.put("good", "good.value");

    String expanded;

    expanded = TemplatingUtility.expandTemplate("No variables", values);
    assertEquals("Incorrect template expansion", "No variables", expanded);

    expanded = TemplatingUtility.expandTemplate("${one} variable", values);
    assertEquals("Incorrect template expansion", "one.value variable", expanded);

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
    assertEquals("Incorrect template expansion", "Has ${bad variable", expanded);

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
    assertEquals("Variable not found", "bad and ${good", sortedVariables.get(0));

    variables = TemplatingUtility.extractTemplateVariables("Has bad} variable");
    sortedVariables = getSortedVariables(variables);
    assertEquals("Incorrect number of variables found", 0, variables.size());
  }

  private List<String> getSortedVariables(final Set<String> variables)
  {
    final List<String> sortedVariables = new ArrayList<String>(variables);
    Collections.sort(sortedVariables);
    return sortedVariables;
  }

}
