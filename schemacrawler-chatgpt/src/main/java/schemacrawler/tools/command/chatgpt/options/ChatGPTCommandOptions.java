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

package schemacrawler.tools.command.chatgpt.options;

import static us.fatehi.utility.Utility.requireNotBlank;
import schemacrawler.tools.executable.CommandOptions;

public class ChatGPTCommandOptions implements CommandOptions {

  private final String apiKey;
  private final String model;

  public ChatGPTCommandOptions(final String apiKey, final String model) {
    this.apiKey = requireNotBlank(apiKey, "No OpenAI API key provided");
    this.model = requireNotBlank(model, "No ChatGPT model provided");
  }

  public String getApiKey() {
    return apiKey;
  }

  public String getModel() {
    return model;
  }
}
