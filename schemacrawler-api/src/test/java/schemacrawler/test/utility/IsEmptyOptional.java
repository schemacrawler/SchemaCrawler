/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2019, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test.utility;


import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class IsEmptyOptional<T>
  extends TypeSafeDiagnosingMatcher<Optional<T>>
{

  /**
   * Creates a Matcher that matches an empty Optional.
   */
  public static <T> Matcher<Optional<T>> emptyOptional()
  {
    return new IsEmptyOptional<>();
  }

  @Override
  public void describeTo(final Description description)
  {
    description.appendText("an Optional<T> that is empty");
  }

  @Override
  protected boolean matchesSafely(final Optional<T> item,
                                  final Description mismatchDescription)
  {
    if (item.isPresent())
    {
      mismatchDescription.appendText("was present with ")
        .appendValue(item.get());
      return false;
    }
    return true;
  }

}
