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
package schemacrawler.test;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

import sf.util.Color;

public class TestColor
{

  @Test
  public void colors()
  {
    final Color color1 = Color.fromRGB(0, 0, 0);
    assertEquals("#000000", color1.toString());

    final Color color2 = Color.fromRGB(0, 0, 255);
    assertEquals("#0000FF", color2.toString());

    final Color color3 = Color.fromRGB(0, 9, 255);
    assertEquals("#0009FF", color3.toString());

    final Color color4 = Color.fromRGB(12, 0, 1);
    assertEquals("#0C0001", color4.toString());
  }

}
