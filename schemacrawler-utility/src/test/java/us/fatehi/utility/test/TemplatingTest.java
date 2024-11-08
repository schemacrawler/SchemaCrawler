/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
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

package us.fatehi.utility.test;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import us.fatehi.utility.TemplatingUtility;

public class TemplatingTest {

  @Test
  public void expandTemplate() throws Exception {
    final Map<String, String> values = new HashMap<>();
    values.put("one", "one.value");
    values.put("another", "two.value");
    values.put("unusual", "10");
    values.put("good", "good.value");
    values.put("split-name", "split-name value");

    String expanded;

    expanded = TemplatingUtility.expandTemplate("No variables", values);
    assertThat("Incorrect template expansion", expanded, is("No variables"));

    expanded = TemplatingUtility.expandTemplate("${ one } ${another } ${ unusual} ${good}", values);
    assertThat("Incorrect template expansion", expanded, is("one.value two.value 10 good.value"));

    expanded = TemplatingUtility.expandTemplate("${ one } variable", values);
    assertThat("Incorrect template expansion", expanded, is("one.value variable"));

    expanded =
        TemplatingUtility.expandTemplate(
            "Has ${ one } variable, and ${ another } variable", values);
    assertThat(
        "Incorrect template expansion",
        expanded,
        is("Has one.value variable, and two.value variable"));

    expanded = TemplatingUtility.expandTemplate("Has $${ unusual } variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has $10 variable"));

    expanded = TemplatingUtility.expandTemplate("Has ${ unusual } } variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has 10 } variable"));

    expanded = TemplatingUtility.expandTemplate("Has ${\t unusual \t\n} variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has 10 variable"));

    expanded = TemplatingUtility.expandTemplate("Has ${ bad variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has ${ bad variable"));

    expanded = TemplatingUtility.expandTemplate("Has ${ good } and ${ bad variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has good.value and ${ bad variable"));

    expanded = TemplatingUtility.expandTemplate("Has ${ bad and ${ good } variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has ${ bad and ${ good } variable"));

    expanded = TemplatingUtility.expandTemplate("Has bad } variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has bad } variable"));

    expanded = TemplatingUtility.expandTemplate("Has ${ undefined } variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has ${ undefined } variable"));

    expanded = TemplatingUtility.expandTemplate("Has ${ split-name } variable", values);
    assertThat("Incorrect template expansion", expanded, is("Has split-name value variable"));
  }

  @Test
  public void extractTemplateVariables() throws Exception {

    Set<String> variables;

    variables = TemplatingUtility.extractTemplateVariables("No variables");
    assertThat("Incorrect number of variables found", variables, is(empty()));

    variables = TemplatingUtility.extractTemplateVariables("${ one } variable");
    assertThat("Incorrect number of variables found", variables, hasSize(1));
    assertThat("Variable not found", variables, hasItems("one"));

    variables =
        TemplatingUtility.extractTemplateVariables(
            "Here are four variables: ${ one } ${two } ${ three} ${four}");
    assertThat("Incorrect number of variables found", variables, hasSize(4));
    assertThat("Variable not found", variables, hasItems("one", "two", "three", "four"));

    variables = TemplatingUtility.extractTemplateVariables("Has $${ unusual } variable");
    assertThat("Incorrect number of variables found", variables, hasSize(1));
    assertThat("Variable not found", variables, hasItems("unusual"));

    variables = TemplatingUtility.extractTemplateVariables("Has ${ unusual } } variable");
    assertThat("Incorrect number of variables found", variables, hasSize(1));
    assertThat("Variable not found", variables, hasItems("unusual"));

    variables = TemplatingUtility.extractTemplateVariables("Has ${ bad variable");
    assertThat("Incorrect number of variables found", variables, is(empty()));

    variables = TemplatingUtility.extractTemplateVariables("Has ${ good } and ${ bad variable");
    assertThat("Incorrect number of variables found", variables, hasSize(1));
    assertThat("Variable not found", variables, hasItems("good"));

    variables = TemplatingUtility.extractTemplateVariables("Has ${ bad and ${ good } variable");
    assertThat("Incorrect number of variables found", variables, hasSize(1));
    assertThat("Variable not found", variables, hasItems("bad and ${ good"));

    variables = TemplatingUtility.extractTemplateVariables("Has bad } variable");
    assertThat("Incorrect number of variables found", variables, is(empty()));
  }
}
