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
package schemacrawler.test;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

import schemacrawler.schema.AttributedObject;
import schemacrawler.schema.NamedObject;
import schemacrawler.tools.lint.Lint;
import schemacrawler.tools.lint.LintSeverity;

public class LintEqualsTest
{

  private final class SimpleNamedObject
    implements NamedObject, AttributedObject
  {

    private static final long serialVersionUID = 1L;

    private final String name;

    public SimpleNamedObject(final String name)
    {
      this.name = name;
    }

    @Override
    public int compareTo(final NamedObject o)
    {
      return 0;
    }

    @Override
    public <T> T getAttribute(final String name)
    {
      return null;
    }

    @Override
    public <T> T getAttribute(final String name, final T defaultValue)
    {
      return null;
    }

    @Override
    public Map<String, Object> getAttributes()
    {
      return null;
    }

    @Override
    public String getFullName()
    {
      return name;
    }

    @Override
    public String getName()
    {
      return name;
    }

    @Override
    public boolean hasAttribute(final String name)
    {
      return false;
    }

    @Override
    public <T> Optional<T> lookupAttribute(final String name)
    {
      return null;
    }

    @Override
    public void removeAttribute(final String name)
    {

    }

    @Override
    public <T> void setAttribute(final String name, final T value)
    {

    }

    @Override
    public List<String> toUniqueLookupKey()
    {
      return new ArrayList<>();
    }

  }

  @Test
  public void lintEquals()
  {
    final Lint<String> testObject1 = new Lint<>("linterId",
                                                "linterInstanceId",
                                                new SimpleNamedObject("test1"),
                                                LintSeverity.critical,
                                                "message",
                                                "");
    final Lint<String> testObject2 = new Lint<>("linterId",
                                                "linterInstanceId",
                                                new SimpleNamedObject("test1"),
                                                LintSeverity.critical,
                                                "message",
                                                "");
    final Lint<String> testObject3 = new Lint<>("linterId",
                                                "linterInstanceId",
                                                new SimpleNamedObject("test2"),
                                                LintSeverity.critical,
                                                "message",
                                                "");
    final Lint<String> testObject4 = new Lint<>("linterId1",
                                                "linterInstanceId",
                                                new SimpleNamedObject("test1"),
                                                LintSeverity.critical,
                                                "message",
                                                "");
    final Lint<File> testObject5 = new Lint<>("linterId",
                                              "linterInstanceId",
                                              new SimpleNamedObject("test1"),
                                              LintSeverity.critical,
                                              "message",
                                              new File(""));
    final Lint<String> testObject6 = new Lint<>("linterId",
                                                "linterInstanceId",
                                                new SimpleNamedObject("test1"),
                                                LintSeverity.high,
                                                "message",
                                                "");
    final Lint<String> testObject7 = new Lint<>("linterId",
                                                "linterInstanceId",
                                                new SimpleNamedObject("test1"),
                                                LintSeverity.critical,
                                                "message1",
                                                "");

    final EqualsTester equalsTester = new EqualsTester()
      .addEqualityGroup(testObject1, testObject2).addEqualityGroup(testObject3)
      .addEqualityGroup(testObject4).addEqualityGroup(testObject5)
      .addEqualityGroup(testObject6).addEqualityGroup(testObject7);
    equalsTester.testEquals();
  }

}
