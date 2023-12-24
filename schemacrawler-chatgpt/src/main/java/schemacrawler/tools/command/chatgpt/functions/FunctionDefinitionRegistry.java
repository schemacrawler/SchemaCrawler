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

package schemacrawler.tools.command.chatgpt.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;
import schemacrawler.tools.command.chatgpt.FunctionDefinition;
import us.fatehi.utility.string.StringFormat;

/** Registry for function definitions. */
public final class FunctionDefinitionRegistry implements Iterable<FunctionDefinition> {

  private static final Logger LOGGER = Logger.getLogger(FunctionDefinitionRegistry.class.getName());

  private static FunctionDefinitionRegistry functionDefinitionRegistrySingleton;

  public static FunctionDefinitionRegistry getFunctionDefinitionRegistry() {
    if (functionDefinitionRegistrySingleton == null) {
      functionDefinitionRegistrySingleton = new FunctionDefinitionRegistry();
    }
    return functionDefinitionRegistrySingleton;
  }

  private static Map<String, FunctionDefinition> loadFunctionDefinitionRegistry() {

    final Map<String, FunctionDefinition> functionDefinitionRegistry = new HashMap<>();

    try {
      final ServiceLoader<FunctionDefinition> serviceLoader =
          ServiceLoader.load(
              FunctionDefinition.class, FunctionDefinitionRegistry.class.getClassLoader());
      for (final FunctionDefinition functionDefinition : serviceLoader) {
        final String functionName = functionDefinition.getName();
        try {
          LOGGER.log(
              Level.CONFIG,
              new StringFormat(
                  "Loading function definition, %s=%s",
                  functionName, functionDefinition.getClass().getName()));
          // Put in map
          functionDefinitionRegistry.put(functionName, functionDefinition);
        } catch (final Exception e) {
          LOGGER.log(
              Level.CONFIG,
              e,
              new StringFormat(
                  "Could not load function definition, %s=%s",
                  functionName, functionDefinition.getClass().getName()));
        }
      }
    } catch (final Exception e) {
      throw new InternalRuntimeException("Could not load function definition registry", e);
    }

    LOGGER.log(
        Level.CONFIG,
        new StringFormat("Loaded %d function definitions", functionDefinitionRegistry.size()));

    return functionDefinitionRegistry;
  }

  private final Map<String, FunctionDefinition> functionDefinitionRegistry;

  private FunctionDefinitionRegistry() {
    functionDefinitionRegistry = loadFunctionDefinitionRegistry();
  }

  @Override
  public Iterator<FunctionDefinition> iterator() {
    final List<FunctionDefinition> functionDefinitions = new ArrayList<>();
    functionDefinitions.addAll(functionDefinitionRegistry.values());
    return functionDefinitions.iterator();
  }
}
