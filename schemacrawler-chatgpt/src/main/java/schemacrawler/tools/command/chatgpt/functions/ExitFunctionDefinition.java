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

package schemacrawler.tools.command.chatgpt.functions;

import java.util.function.Function;
import schemacrawler.tools.command.chatgpt.functions.ExitFunctionDefinition.ExitFunctionParameters;

public final class ExitFunctionDefinition
    extends AbstractFunctionDefinition<ExitFunctionParameters> {

  public static class ExitFunctionParameters implements FunctionParameters {}

  public ExitFunctionDefinition() {
    super(
        "exit",
        "Called when the user is done with their research, wants to end the chat session.",
        ExitFunctionParameters.class);
  }

  @Override
  public Function<ExitFunctionParameters, FunctionReturn> getExecutor() {
    return args -> () -> "Thank you for using SchemaCrawler with ChatGPT.";
  }
}
