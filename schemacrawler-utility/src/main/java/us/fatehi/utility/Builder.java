/*
 * SchemaCrawler
 * http://www.schemacrawler.com
 * Copyright (c) 2000-2025, Sualeh Fatehi <sualeh@hotmail.com>.
 * All rights reserved.
 * SPDX-License-Identifier: EPL-2.0
 */

package us.fatehi.utility;

/**
 * Convoluted interface to allow for subclasses builders, while maintaining a fluent interface.
 *
 * @param <B> Builder
 * @param <O> Options to be built
 * @see <a href=
 *     "https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class">Subclassing a
 *     Java Builder class</a>
 */
public interface Builder<O> {

  O build();
}
