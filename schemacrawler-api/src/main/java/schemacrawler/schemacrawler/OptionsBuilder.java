/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2026, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package schemacrawler.schemacrawler;

import us.fatehi.utility.Builder;

/**
 * Convoluted interface to allow for subclasses builders, while maintaining a fluent interface.
 *
 * @param <B> Builder
 * @param <O> Options to be built
 * @see <a href=
 *     "https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class">Subclassing a
 *     Java Builder class</a>
 */
public interface OptionsBuilder<B extends OptionsBuilder<B, O>, O extends Options>
    extends Builder<O> {

  OptionsBuilder<B, O> fromOptions(O options);

  O toOptions();

  @Override
  default O build() {
    return toOptions();
  }
}
