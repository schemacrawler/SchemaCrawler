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

package schemacrawler.tools.command.chatgpt.systemfunctions;

import static schemacrawler.tools.command.chatgpt.FunctionDefinition.FunctionType.SYSTEM;
import java.util.function.Function;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import schemacrawler.tools.command.chatgpt.FunctionReturn;
import schemacrawler.tools.command.chatgpt.functions.AbstractFunctionDefinition;
import schemacrawler.tools.command.chatgpt.functions.NoFunctionParameters;

public class AgentFunctionDefinition extends AbstractFunctionDefinition<NoFunctionParameters> {

  public AgentFunctionDefinition() {
    super(
        "Instructs the agent to act as an expert on the database schema.",
        NoFunctionParameters.class);
  }

  @Override
  public Function<NoFunctionParameters, FunctionReturn> getExecutor() {
    return args ->
        () ->
            "The agent is an expert on relational databases, and can advise on the particular database schema that is provided.";
  }

  @Override
  public FunctionType getFunctionType() {
    return SYSTEM;
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (final JsonProcessingException e) {
      return super.toString();
    }
  }
}
