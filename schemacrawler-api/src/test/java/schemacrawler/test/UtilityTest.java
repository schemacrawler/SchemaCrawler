/*
========================================================================
SchemaCrawler
http://www.schemacrawler.com
Copyright (c) 2000-2017, Sualeh Fatehi <sualeh@hotmail.com>.
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
package schemacrawler.test;


import static org.junit.Assert.assertTrue;
import static sf.util.Utility.isBlank;

import org.junit.Test;

public class UtilityTest
{

  @Test
  public void isBlankTest()
  {
    assertTrue(isBlank(null));
    assertTrue(isBlank(""));
    assertTrue(isBlank(" "));
    assertTrue(isBlank("   "));
    assertTrue(isBlank("\t"));
    assertTrue(isBlank("\n"));
    assertTrue(isBlank("\r"));
    assertTrue(isBlank(" \t "));
    assertTrue(isBlank("\t\t"));

    assertTrue(!isBlank("a"));
    assertTrue(!isBlank("©"));
    assertTrue(!isBlank(" a"));
    assertTrue(!isBlank("a "));
    assertTrue(!isBlank("a b"));
  }

}
