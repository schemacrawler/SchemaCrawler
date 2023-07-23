/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2023, Sualeh Fatehi <sualeh@hotmail.com>.
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

package schemacrawler.tools.command.chatgpt.test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import schemacrawler.tools.command.chatgpt.FunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectDescriptionFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.DatabaseObjectListFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.ExitFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.FunctionDefinitionRegistry;
import schemacrawler.tools.command.chatgpt.functions.LintFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.TableDecriptionFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.TableReferencesFunctionDefinition;
import schemacrawler.tools.command.chatgpt.systemfunctions.SchemaFunctionDefinition;

public class FunctionDefinitionRegistryTest {

  public static <T> Collection<T> convertIterableToCollection(final Iterable<T> iterable) {
    final Collection<T> collection = new ArrayList<>();
    for (final T element : iterable) {
      collection.add(element);
    }
    return collection;
  }

  @Test
  public void testCommandPlugin() throws Exception {
    final FunctionDefinitionRegistry registry =
        FunctionDefinitionRegistry.getFunctionDefinitionRegistry();
    final Collection<FunctionDefinition> functions = convertIterableToCollection(registry);
    assertThat(functions, hasSize(7));
    assertThat(
        functions,
        containsInAnyOrder(
            new DatabaseObjectListFunctionDefinition(),
            new TableDecriptionFunctionDefinition(),
            new TableReferencesFunctionDefinition(),
            new DatabaseObjectDescriptionFunctionDefinition(),
            new LintFunctionDefinition(),
            new ExitFunctionDefinition(),
            new SchemaFunctionDefinition()));
  }
}
