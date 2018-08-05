/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2018, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.schemacrawler;


/**
 * Convoluted interface to allow for subclasses builders, while
 * maintaining a fluent interface.
 *
 * @see <a href=
 *      "https://stackoverflow.com/questions/17164375/subclassing-a-java-builder-class">Subclassing
 *      a Java Builder class</a>
 * @author Sualeh Fatehi
 * @param <B>
 *        Builder
 * @param <O>
 *        Options to be built
 */
public interface OptionsBuilder<B extends OptionsBuilder<B, O>, O extends Options>
{

  OptionsBuilder<B, O> fromConfig(Config config);

  OptionsBuilder<B, O> fromOptions(O options);

  Config toConfig();

  O toOptions();

}
