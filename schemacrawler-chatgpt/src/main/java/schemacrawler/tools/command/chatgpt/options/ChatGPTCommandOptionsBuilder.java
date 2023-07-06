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

import static us.fatehi.utility.Utility.isBlank;
import schemacrawler.schemacrawler.OptionsBuilder;
import schemacrawler.tools.options.Config;
import schemacrawler.tools.options.ConfigOptionsBuilder;
import us.fatehi.utility.PropertiesUtility;

public final class ChatGPTCommandOptionsBuilder
    implements OptionsBuilder<ChatGPTCommandOptionsBuilder, ChatGPTCommandOptions>,
        ConfigOptionsBuilder<ChatGPTCommandOptionsBuilder, ChatGPTCommandOptions> {

  private static final int DEFAULT_CONTEXT = 10;

  public static ChatGPTCommandOptionsBuilder builder() {
    return new ChatGPTCommandOptionsBuilder();
  }

  private String apiKey;
  private String model;
  private int context;

  private ChatGPTCommandOptionsBuilder() {
    model = "gpt-3.5-turbo";
    context = DEFAULT_CONTEXT;
  }

  @Override
  public ChatGPTCommandOptionsBuilder fromConfig(final Config config) {
    if (config != null) {
      apiKey = getApiKey(config);
      model = config.getStringValue("model", model);
      context = config.getIntegerValue("context", DEFAULT_CONTEXT);
    }

    return this;
  }

  @Override
  public ChatGPTCommandOptionsBuilder fromOptions(final ChatGPTCommandOptions options) {
    if (options != null) {
      apiKey = options.getApiKey();
      model = options.getModel();
    }
    return this;
  }

  @Override
  public Config toConfig() {
    // Not implemented, since we cannot (should not) write API key to config
    throw new UnsupportedOperationException();
  }

  @Override
  public ChatGPTCommandOptions toOptions() {
    return new ChatGPTCommandOptions(apiKey, model, context);
  }

  /**
   * Use the provided OpenAI API key is it is not blank.
   *
   * @param apiKey OpenAI API key.
   * @return Self.
   */
  public ChatGPTCommandOptionsBuilder withApiKey(final String apiKey) {
    if (!isBlank(apiKey)) {
      this.apiKey = apiKey;
    }
    return this;
  }

  /**
   * Use the provided OpenAI API key is it is not blank.
   *
   * @param apiKey OpenAI API key.
   * @return Self.
   */
  public ChatGPTCommandOptionsBuilder withContext(final int context) {
    this.context = context;
    return this;
  }

  /**
   * Use the provided ChatGPT model is it is not blank.
   *
   * @param apiKey ChatGPT model.
   * @return Self.
   */
  public ChatGPTCommandOptionsBuilder withModel(final String model) {
    if (!isBlank(model)) {
      this.model = model;
    }
    return this;
  }

  private String getApiKey(final Config config) {
    String apiKey = config.getStringValue("api-key", null);
    if (isBlank(apiKey)) {
      final String apikeyVar = config.getStringValue("api-key:env", null);
      if (!isBlank(apikeyVar)) {
        apiKey = PropertiesUtility.getSystemConfigurationProperty(apikeyVar, null);
      }
    }
    return apiKey;
  }
}
