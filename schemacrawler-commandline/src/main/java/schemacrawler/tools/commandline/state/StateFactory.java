/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
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
      throw new InternalRuntimeException(String.format("Could not instantiate class <%s>", cls), e);
    }
  }
}
