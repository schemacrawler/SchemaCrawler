/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2024, Sualeh Fatehi <sualeh@hotmail.com>.
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

  private static final int DEFAULT_CONTEXT = 10;
  static final int MAXIMUM_CONTEXT = 50;

  private final String apiKey;
  private final String model;
  private final int context;
  private final boolean useMetadata;

  public ChatGPTCommandOptions(
      final String apiKey, final String model, final int context, final boolean useMetadata) {
    this.apiKey = requireNotBlank(apiKey, "No OpenAI API key provided");
    this.model = requireNotBlank(model, "No ChatGPT model provided");
    if (context <= 0 || context > MAXIMUM_CONTEXT) {
      this.context = DEFAULT_CONTEXT;
    } else {
      this.context = context;
    }
    this.useMetadata = useMetadata;
  }

  public String getApiKey() {
    return apiKey;
  }

  public int getContext() {
    return context;
  }

  public String getModel() {
    return model;
  }

  public boolean isUseMetadata() {
    return useMetadata;
  }
}
