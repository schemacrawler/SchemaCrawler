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
package schemacrawler.tools.commandline.state;

import static picocli.CommandLine.defaultFactory;

import picocli.CommandLine.IFactory;
import schemacrawler.schemacrawler.exceptions.InternalRuntimeException;

public class StateFactory extends BaseStateHolder implements IFactory {

  private static IFactory defaultPicocliFactory = defaultFactory();

  public StateFactory(final ShellState state) {
    super(state);
  }

  @Override
  public <K> K create(final Class<K> cls) {
    try {
      if (cls == null) {
        return null;
      } else if (BaseStateHolder.class.isAssignableFrom(cls)) {
        return cls.getConstructor(ShellState.class).newInstance(state);
      } else {
        return defaultPicocliFactory.create(cls);
      }
    } catch (final Exception e) {
      throw new InternalRuntimeException(
          String.format("Could not instantiate class <%s>", cls), e);
    }
  }
}
