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


import static org.junit.Assert.assertTrue;

import org.junit.Test;

import sf.util.Utility;

public class UtilityTest
{

  @Test
  public void isBlank()
  {
    assertTrue(Utility.isBlank(null));
    assertTrue(Utility.isBlank(""));
    assertTrue(Utility.isBlank(" "));
    assertTrue(Utility.isBlank("   "));
    assertTrue(Utility.isBlank("\t"));
    assertTrue(Utility.isBlank("\n"));
    assertTrue(Utility.isBlank("\r"));
    assertTrue(Utility.isBlank(" \t "));
    assertTrue(Utility.isBlank("\t\t"));

    assertTrue(!Utility.isBlank("a"));
    assertTrue(!Utility.isBlank("©"));
    assertTrue(!Utility.isBlank(" a"));
    assertTrue(!Utility.isBlank("a "));
    assertTrue(!Utility.isBlank("a b"));
  }

}
