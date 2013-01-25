/*
 * SchemaCrawler
 * Copyright (c) 2000-2013, Sualeh Fatehi.
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
