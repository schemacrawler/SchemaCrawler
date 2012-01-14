/*
 * SchemaCrawler
 * Copyright (c) 2000-2012, Sualeh Fatehi.
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


import schemacrawler.schemacrawler.Config;
import schemacrawler.schemacrawler.SchemaCrawlerException;

public class ConfigLoader
{

  public static Config configFromResource(final String resource)
    throws SchemaCrawlerException
  {
    if (resource == null)
    {
      throw new SchemaCrawlerException("No config resource provided");
    }
    return Config.load(ConfigLoader.class.getResourceAsStream(resource));
  }

  private ConfigLoader()
  {
  }

}
