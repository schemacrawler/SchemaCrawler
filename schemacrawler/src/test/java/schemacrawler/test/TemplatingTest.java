/*
 * SchemaCrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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
import java.util.List;
import java.util.Set;

import org.junit.Test;

import sf.util.TemplatingUtility;

public class TemplatingTest
{

  @Test
  public void extractTemplateVariables()
    throws Exception
  {
    Set<String> variables;
    List<String> sortedVariables;

    variables = TemplatingUtility.extractTemplateVariables("No variables");
    assertEquals("Incorrect number of variables found", variables.size(), 0);

    variables = TemplatingUtility.extractTemplateVariables("${one} variable");
    sortedVariables = new ArrayList<String>(variables);
    assertEquals("Incorrect number of variables found", variables.size(), 1);
    assertEquals("Variable not found", sortedVariables.get(0), "one");

    variables = TemplatingUtility
      .extractTemplateVariables("Has ${one} variable, and ${another} variable");
    sortedVariables = new ArrayList<String>(variables);
    assertEquals("Incorrect number of variables found", variables.size(), 2);
    assertEquals("Variable not found", sortedVariables.get(0), "another");
    assertEquals("Variable not found", sortedVariables.get(1), "one");

  }

}
