/*
 * SchemaCrawler
 * Copyright (c) 2000-2009, Sualeh Fatehi.
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


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Driver;
import java.util.List;

import org.junit.Test;

import sf.util.ImplementationFinder;

public class DriverFinderTest
{

  @Test
  public void jdbcDrivers()
    throws Exception
  {
    final ImplementationFinder finder = new ImplementationFinder(Driver.class);
    final List<Class<Object>> classes = finder.findImplementations();

    assertNotNull("No JDBC driver implementation found", classes);
    assertEquals("Incorrect number of JDBC driver classes found", 1, classes
      .size());
    assertEquals("Incorrect JDBC driver class found",
                 "org.hsqldb.jdbcDriver",
                 classes.get(0).getName());
  }

}
